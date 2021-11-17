package me.dzikimlecz.touchclient.mainview;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import me.dzikimlecz.touchclient.model.UserProfile;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ChatListView implements Initializable {
    @FXML
    ListView<UserProfile> list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<UserProfile> profiles = FXCollections.observableArrayList(
                UserProfile.of("LilPope", 2137),
                UserProfile.of("NeverSeenCatOOO", 5318008)
        );
        list.setCellFactory(listView -> new ListCell<>() {
            @Override protected void updateItem(UserProfile item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) setGraphic(getChat(item));
            }
        });
        list.setItems(profiles);
    }

    private Node getChat(UserProfile profile) {
        try {
            final var loader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
            final Node node = loader.load();
            final ChatView controller = loader.getController();
            controller.setProfile(profile);
            return node;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
