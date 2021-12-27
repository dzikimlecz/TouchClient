package me.dzikimlecz.touchclient.mainview;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;
import me.dzikimlecz.touchclient.model.MessagesHandler;
import me.dzikimlecz.touchclient.model.UserProfile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class MainView implements Initializable {

    @FXML
    public BorderPane root;

    @NotNull
    public static Parent create() throws Exception {
        return FXMLLoader.load(requireNonNull(MainView.class.getResource("main-view.fxml")));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final var nodeController =
                loadAndGetController(getClass().getResource("messages-view.fxml"));
        MessagesView messagesView = nodeController.getValue();
        messagesView.setMessagesHandler(new MessagesHandler(getUserProfile()));
        root.setCenter(nodeController.getKey());
        messagesView.setUserProfile(getUserProfile());
        //todo remove hard coded recipient
        messagesView.setRecipientProfile(UserProfile.of("LilPope", 2137));
    }

    @NotNull
    private UserProfile getUserProfile() {
        return UserProfile.of("NeverSeenCatOOO", 5318008);
    }

    @NotNull
    private static Pair<Node, MessagesView> loadAndGetController(URL resource) {
        final var loader = new FXMLLoader(resource);
        final Node node;
        try {
            node = loader.load();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        final MessagesView controller = loader.getController();
        if (controller == null) throw new IllegalStateException("MessagesView controller is null!");
        return new Pair<>(node, controller);
    }
}
