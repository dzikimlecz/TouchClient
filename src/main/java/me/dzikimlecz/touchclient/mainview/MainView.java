package me.dzikimlecz.touchclient.mainview;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;
import me.dzikimlecz.touchclient.client.ServerHandler;
import me.dzikimlecz.touchclient.model.UserProfile;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;
import static javafx.scene.control.Alert.AlertType.WARNING;
import static me.dzikimlecz.touchclient.TouchApp.loadAndGetController;
import static me.dzikimlecz.touchclient.client.ProfilesCache.cacheUser;
import static me.dzikimlecz.touchclient.model.UserProfile.getUsername;
import static me.dzikimlecz.touchclient.model.UserProfile.parseTag;

public class MainView implements Initializable {

    @FXML
    public BorderPane root;
    @FXML
    public BorderPane usersPane;
    @FXML
    public TextField newMessageField;
    private UserProfile userProfile;

    private ChatListView chatList;
    private final ServerHandler serverHandler = new ServerHandler(getUserProfile());

    @NotNull
    public static Parent create() throws Exception {
        return FXMLLoader.load(requireNonNull(MainView.class.getResource("main-view.fxml")));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final Pair<Parent, MessagesView> messagesView =
                loadAndGetController("messages-view.fxml");
        var messagesViewController = messagesView.getValue();
        messagesViewController.setServerHandler(serverHandler);
        root.setCenter(messagesView.getKey());
        messagesViewController.setUserProfile(getUserProfile());
        final Pair<Parent, ChatListView> chatList =
                loadAndGetController("chatlist-view.fxml");
        this.chatList = chatList.getValue();
        usersPane.setCenter(chatList.getKey());
        messagesViewController.recipientProfileProperty()
                .bind(this.chatList.selectedItemProperty());
    }



    @FXML
    protected void toNewUser(@SuppressWarnings("unused") ActionEvent __) {
        final var text = newMessageField.getText().trim();
        newMessageField.clear();
        final var strings = text.split("#");
        final var profile = UserProfile.of(getUsername(strings), parseTag(strings[1]));
        if (serverHandler.doesProfileExist(profile)) {
            cacheUser(profile);
            chatList.addProfile(profile);
            chatList.select(profile);
        } else new Alert(WARNING, "Can't find user " + profile.getNameTag()).show();
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
