package me.dzikimlecz.touchclient.mainview;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;
import me.dzikimlecz.touchclient.model.Message;
import me.dzikimlecz.touchclient.model.UserProfile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;

public class MainView implements Initializable {

    @FXML
    public BorderPane root;
    private MessagesView messagesView;

    @NotNull
    public static Parent create() throws Exception {
        return FXMLLoader.load(requireNonNull(MainView.class.getResource("main-view.fxml")));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final var nodeController =
                loadAndGetController(getClass().getResource("messages-view.fxml"));
        messagesView = nodeController.getValue();
        root.setCenter(nodeController.getKey());
        messagesView.setUserProfile(getUserProfile());
        messagesView.setRecipientProfile(UserProfile.of("LilPope", 2137));
        messagesView.setMessages(loadMessages());
    }

    @NotNull
    private UserProfile getUserProfile() {
        return UserProfile.of("NeverSeenCatOOO", 5318008);
    }

    @NotNull
    private List<Message> loadMessages() {
        return List.of(
                new Message(
                        UserProfile.of("LilPope", 2137),
                        UserProfile.of("NeverSeenCatOOO", 5318008),
                        "I'm Bored",
                        now().minusMinutes(3)
                ),
                new Message(
                        UserProfile.of("LilPope", 2137),
                        UserProfile.of("NeverSeenCatOOO", 5318008),
                        "Come Over",
                        now().minusMinutes(2)
                ),
                new Message(
                        UserProfile.of("NeverSeenCatOOO", 5318008),
                        UserProfile.of("LilPope", 2137),
                        "No",
                        now().minusMinutes(1)
                )
        );
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
