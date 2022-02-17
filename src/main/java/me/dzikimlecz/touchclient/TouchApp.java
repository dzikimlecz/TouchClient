package me.dzikimlecz.touchclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Pair;
import me.dzikimlecz.touchclient.config.Env;
import me.dzikimlecz.touchclient.mainview.LoginView;
import me.dzikimlecz.touchclient.mainview.MainView;
import me.dzikimlecz.touchclient.model.UserProfile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class TouchApp extends Application {
    private Scene scene;

    @Override
    public void init() {
        Env.load();
    }

    @Override
    public void start(Stage stage) {
        stage.setMinHeight(300);
        stage.setMinWidth(400);
        stage.setTitle("Touch");
        stage.getIcons().add(new Image(requireNonNull(getClass().getResourceAsStream("touch.png"))));
        final Pair<Parent, LoginView> loginView =
                loadAndGetController("login-view.fxml");
        var loginViewControl = loginView.getValue();
        loginViewControl.setApp(this);
        stage.setScene(scene = new Scene(loginView.getKey()));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void login(UserProfile profile) {
        final Pair<Parent, MainView> mainView =
                loadAndGetController("main-view.fxml");
        var mainViewControl = mainView.getValue();
        mainViewControl.setUserProfile(profile);
        scene.setRoot(mainView.getKey());
    }

    @NotNull
    public static  <T> Pair<Parent, T> loadAndGetController(String resource) {
        final var loader = new FXMLLoader(MainView.class.getResource(resource));
        final Parent node;
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
}
