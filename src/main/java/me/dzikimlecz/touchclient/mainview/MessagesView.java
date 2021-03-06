package me.dzikimlecz.touchclient.mainview;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import me.dzikimlecz.touchclient.client.ConnectionException;
import me.dzikimlecz.touchclient.client.MessageContainer;
import me.dzikimlecz.touchclient.client.ResponseException;
import me.dzikimlecz.touchclient.model.Message;
import me.dzikimlecz.touchclient.client.ServerHandler;
import me.dzikimlecz.touchclient.model.UserProfile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.LocalDateTime.now;
import static javafx.collections.FXCollections.observableList;
import static javafx.scene.control.Alert.AlertType.*;
import static me.dzikimlecz.touchclient.model.Message.NULL_MESSAGE;
import static me.dzikimlecz.touchclient.model.message.MessageDisplayMode.*;

public class MessagesView implements Initializable {

    @FXML
    public ListView<Message> messagesList;
    @FXML
    public TextArea messageArea;

    private final ObjectProperty<UserProfile> userProfile = new SimpleObjectProperty<>();
    public ObjectProperty<UserProfile> recipientProfileProperty() {
        return recipientProfile;
    }

    private final ObjectProperty<UserProfile> recipientProfile = new SimpleObjectProperty<>();
    private final AtomicInteger loaded = new AtomicInteger();
    private final BooleanProperty nullsReported = new SimpleBooleanProperty(false);
    private final AtomicBoolean nullsShown = new AtomicBoolean(false);

    private ServerHandler serverHandler;
    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public void setUserProfile(UserProfile profile) {
        userProfile.set(profile);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messagesList.setItems(observableList(new LinkedList<>()));
        populate();
        messagesList.setSelectionModel(NoSelectionModel.get());
        messagesList.setCellFactory(this::messagesCellFactory);
        recipientProfile.addListener((obs, oldVal, newVal) -> {
            messagesList.getItems().clear();
            if (newVal == null) {
                nullsHandled();
                return;
            }
            populate();
            loaded.set(0);
            loadMore(newVal);
            scrollToBottom();
        });
        messagesList.setOnScroll( event -> {
            if (nullsShown.get()) {
                final var totalDeltaY = event.getDeltaY();
                if (totalDeltaY < -20) nullsReported.set(false);
                else if (totalDeltaY > 20) nullsReported.set(true);
            }
        });
        nullsReported.addListener(((observable, oldValue, newValue) -> {
            if (newValue) loadMore(recipientProfile.get());
        }));
    }

    private void populate() {
        var nullList = new ArrayList<Message>();
        for (int i = 0; i < 10; i++) nullList.add(NULL_MESSAGE);
        messagesList.getItems().addAll(nullList);
    }

    private void loadMore(UserProfile from) {
        try {
            serverHandler.loadNew();
        }
        catch (ConnectionException e) {
            new Alert(ERROR, "Could not connect to the server.").show();
            return;
        } catch (ResponseException e) {
            new Alert(ERROR, e.getMessage()).show();
            return;
        }
        try {
            serverHandler.loadOlder(from);
        } catch (ResponseException e) {
            if (e.getStatusCode() == 404)
                new Alert(ERROR, "Can't find user " + from.getNameTag()).show();
            return;
        } catch (NoSuchElementException ignore) {}

        serverHandler.getConversation(from, 0).ifPresent(messages -> {
            final var items = messagesList.getItems();
            if (items.stream().anyMatch(message -> !messages.getElements().contains(message))) {
                final var newMessages = new ArrayList<>(messages.getElements());
                newMessages.removeAll(items);
                addMessages(newMessages);
            } else {
                final var conversation = serverHandler.getConversation(from, loaded.get());
                conversation.ifPresent(messages1 -> {
                    loaded.incrementAndGet();
                    addMessages(messages1.getElements());
                    if (messages1.getSize() >= 15)
                        nullsHandled();
                });
            }
        });
    }

    private void scrollToBottom() {
        messagesList.scrollTo(messagesList.getItems().size() - 1);
    }

    private void addMessage(@NotNull Message msg) {
        messagesList.getItems().add(msg);
        sortMessagesList();
    }

    private void addMessages(List<Message> messages) {
        if (messages.isEmpty()) return;
        messagesList.getItems().addAll(messages);
        sortMessagesList();
    }

    private void sortMessagesList() {
        messagesList.getItems().sort((m1, m2) -> {
            if (m1.equals(m2)) return 0;
            if (m1 == NULL_MESSAGE) return -1;
            return m1.getSentOn().compareTo(m2.getSentOn());
        });
    }

    @Contract(pure = true)
    private @Nullable Node loadMessageContainer(Message msg) {
        var loader = new FXMLLoader(getClass().getResource("message-container.fxml"));
        Node node;
        try {
            node = loader.load();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        MessageContainer container = loader.getController();
        var displayMode =
                NULL_MESSAGE.equals(msg) ? NULL :
                        userProfile.get().equals(msg.getSender()) ?
                            AS_SENDER : AS_RECIPIENT;
        container.put(msg, displayMode);
        return node;
    }

    @FXML
    protected void sendMessage(@SuppressWarnings("unused") ActionEvent __) {
        final String value = getTypedText();
        final var msg = createMessage(value);
        try {
            serverHandler.sendMessage(msg);
        } catch (ResponseException e) {
            if (e.getStatusCode() == 404)
                new Alert(ERROR, "Can't find user " + recipientProfile.get().getNameTag()).show();
            return;
        } catch (ConnectionException e) {
            new Alert(ERROR, "Could not connect to the server.").show();
            return;
        }
        addMessage(msg);
    }

    private String getTypedText() {
        final var stringProperty = messageArea.textProperty();
        final var value = stringProperty.getValue();
        stringProperty.set("");
        return value;
    }

    @NotNull
    private Message createMessage(String value) {
        return new Message(userProfile.get(), recipientProfile.get(), value, now());
    }

    protected ListCell<Message> messagesCellFactory(ListView<Message> listView) {
        return new ListCell<>() {
            // init block (got confused with scoping before)
            {
                setStyle("-fx-padding: 5px; -fx-background-color: #F0F0F0;");
            }

            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    if (NULL_MESSAGE.equals(item))
                        reportNulls();
                    setGraphic(loadMessageContainer(item));
                }
            }
        };
    }

    private void reportNulls() {
        nullsShown.set(true);
    }

    private void nullsHandled() {
        nullsReported.set(false);
        nullsShown.set(false);
    }
}
