package me.dzikimlecz.touchclient.mainview;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;
import me.dzikimlecz.touchclient.model.ServerHandler;
import me.dzikimlecz.touchclient.model.ProfilesCache;
import me.dzikimlecz.touchclient.model.UserProfile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;
import static me.dzikimlecz.touchclient.model.UserProfile.getUsername;
import static me.dzikimlecz.touchclient.model.UserProfile.parseTag;

public class MainView implements Initializable {

    @FXML
    public BorderPane root;
    @FXML
    public BorderPane usersPane;
    @FXML
    public TextField newMessageField;

    private ChatListView chatList;

    @NotNull
    public static Parent create() throws Exception {
        return FXMLLoader.load(requireNonNull(MainView.class.getResource("main-view.fxml")));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final Pair<Node, MessagesView> messagesView =
                loadAndGetController("messages-view.fxml");
        var messagesViewController = messagesView.getValue();
        messagesViewController.setMessagesHandler(new ServerHandler(getUserProfile()));
        root.setCenter(messagesView.getKey());
        messagesViewController.setUserProfile(getUserProfile());
        final Pair<Node, ChatListView> chatList =
                loadAndGetController("chatlist-view.fxml");
        this.chatList = chatList.getValue();
        usersPane.setCenter(chatList.getKey());
        messagesViewController.recipientProfileProperty()
                .bind(this.chatList.selectedItemProperty());
    }

    @NotNull
    private UserProfile getUserProfile() {
        return UserProfile.of("NeverSeenCatOOO", 5318008);
    }

    @NotNull
    private <T> Pair<Node, T> loadAndGetController(String resource) {
        final var loader = new FXMLLoader(getClass().getResource(resource));
        final Node node;
        try {
            node = loader.load();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        final T controller = loader.getController();
        if (controller == null)
            throw new IllegalStateException(resource + " controller must not be null");
        return new Pair<>(node, controller);
    }

    @FXML
    protected void toNewUser(@SuppressWarnings("unused") ActionEvent __) {
        final var text = newMessageField.getText().trim();
        final var strings = text.split("#");
        final var profile = UserProfile.of(getUsername(strings), parseTag(strings[1]));
        ProfilesCache.cacheUser(profile);
        chatList.addProfile(profile);
        chatList.select(profile);
    }
}
