package me.dzikimlecz.touchclient.mainview;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import me.dzikimlecz.touchclient.model.Message;
import me.dzikimlecz.touchclient.model.MessagesHandler;
import me.dzikimlecz.touchclient.model.UserProfile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;
import static javafx.collections.FXCollections.observableArrayList;
import static me.dzikimlecz.touchclient.model.message.MessageDisplayMode.AS_RECIPIENT;
import static me.dzikimlecz.touchclient.model.message.MessageDisplayMode.AS_SENDER;

public class MessagesView implements Initializable {

    @FXML
    public ListView<Message> messagesList;
    @FXML
    public TextArea messageArea;

    private final SimpleObjectProperty<UserProfile> userProfile = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<UserProfile> recipientProfile = new SimpleObjectProperty<>();
    private final AtomicInteger loaded = new AtomicInteger();

    private MessageSender messageSender;

    private MessagesHandler messagesHandler;

    public void setUserProfile(UserProfile profile) {
        userProfile.set(profile);
    }

    public void setRecipientProfile(UserProfile profile) {
        recipientProfile.set(profile);
    }

    public MessagesView() {
        userProfile = new SimpleObjectProperty<>();
        recipientProfile = new SimpleObjectProperty<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messagesList.setSelectionModel(NoSelectionModel.getInstance());
        messagesList.setCellFactory(this::messagesCellFactory);
    }

    public void setMessages(@NotNull List<Message> messages) {
        final var items = observableArrayList(messages);
        items.sort(comparing(Message::getSentOn));
        messagesList.setItems(items);
    }

    public void addMessage(@NotNull Message msg) {
        messagesList.getItems().add(msg);
        messagesList.getItems().sort(comparing(Message::getSentOn));
    }

    public final void addMessages(@NotNull Message msg, Message... messages) {
        messagesList.getItems().add(msg);
        if (messages != null)
            messagesList.getItems().addAll(messages);
        messagesList.getItems().sort(comparing(Message::getSentOn));
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
        var displayMode = userProfile.get().equals(msg.getSender()) ? AS_SENDER : AS_RECIPIENT;
        container.put(msg, displayMode);
        return node;
    }

    @FXML
    protected void sendMessage(ActionEvent __) {
        final String value = getValue();
        final var msg = createMessage(value);
        if (messageSender != null)
            messageSender.send(msg);
        addMessage(msg);
    }

    private String getValue() {
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
            {
                setStyle("-fx-padding: 5px; -fx-background-color: #F0F0F0;");
            }

            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null)
                    setGraphic(loadMessageContainer(item));
            }
        };
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public MessagesHandler getMessagesHandler() {
        return messagesHandler;
    }

    public void setMessagesHandler(MessagesHandler messagesHandler) {
        this.messagesHandler = messagesHandler;
    }
}
