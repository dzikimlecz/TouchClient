package me.dzikimlecz.touchclient.mainview;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
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
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.LocalDateTime.now;
import static javafx.collections.FXCollections.observableList;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messagesList.setItems(observableList(new LinkedList<>()));
        //populate the list
        for (int i = 0; i < 10; i++) messagesList.getItems().add(Message.EMPTY);
        messagesList.setSelectionModel(NoSelectionModel.getInstance());
        messagesList.setCellFactory(this::messagesCellFactory);
        // extracted lambda to a variable, due to the need of warning suppression
        @SuppressWarnings("unchecked")
        final ChangeListener<UserProfile> changeListener = (obs, oldVal, newVal) -> {
            if (newVal == null) {
                ((SimpleObjectProperty<UserProfile>) obs).setValue(oldVal);
                return;
            }
            loaded.set(0);
            try {
                messagesHandler.loadOlder(newVal);
            } catch (NoSuchElementException ignore) {}
            final var conversation = messagesHandler.getConversation(newVal, 0);
            conversation.ifPresent(messages -> {
                loaded.addAndGet(messages.getSize());
                messagesList.getItems().addAll(messages.getElements());
            });
        };
        recipientProfile.addListener(changeListener);
        messagesList.setOnScrollTo(event -> {
            if (event.getScrollTarget() <= 1) {
                final var conversation =
                        messagesHandler.getConversation(recipientProfile.get(), 1);
                conversation.ifPresent(messages -> addMessages(messages.getElements()));
            }
        });
    }

    private void setMessages(@NotNull List<Message> messages) {
        final var items = messagesList.getItems();
        items.clear();
        items.addAll(messages);
        sortMessagesList();
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

    private void addMessages(@NotNull Message msg, Message... messages) {
        messagesList.getItems().add(msg);
        if (messages != null)
            messagesList.getItems().addAll(messages);
        sortMessagesList();
    }

    private void sortMessagesList() {
        messagesList.getItems().sort((m1, m2) -> {
            if (m1.equals(m2)) return 0;
            if (m1 == Message.EMPTY) return -1;
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
        var displayMode = userProfile.get().equals(msg.getSender()) ? AS_SENDER : AS_RECIPIENT;
        container.put(msg, displayMode);
        return node;
    }

    @FXML
    @SuppressWarnings("unused")
    protected void sendMessage(ActionEvent __) {
        final String value = getTypedText();
        final var msg = createMessage(value);
        if (messageSender != null)
            messageSender.send(msg);
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
