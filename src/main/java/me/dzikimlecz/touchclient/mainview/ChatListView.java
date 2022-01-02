package me.dzikimlecz.touchclient.mainview;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import me.dzikimlecz.touchclient.model.UserProfile;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class ChatListView implements Initializable {
    @FXML
    ListView<UserProfile> list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<UserProfile> profiles = observableArrayList();
        list.setCellFactory(listView -> new ListCell<>() {
            @Override protected void updateItem(UserProfile item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) setGraphic(getChat(item));
            }
        });
        list.setItems(profiles);
        list.getSelectionModel().clearSelection();
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

    public ReadOnlyObjectProperty<UserProfile> selectedItemProperty() {
        return list.getSelectionModel().selectedItemProperty();
    }

    public void addProfile(UserProfile profile) {
        list.getItems().add(profile);
    }

    public void addProfiles(Collection<UserProfile> profile) {
        list.getItems().addAll(profile);
    }
}
