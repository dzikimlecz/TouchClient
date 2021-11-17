package me.dzikimlecz.touchclient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import me.dzikimlecz.touchclient.mainview.MainView;

import static java.util.Objects.requireNonNull;

public class TouchApp extends Application {

    @Override
    public void init() {
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setMinHeight(300);
        stage.setMinWidth(400);
        stage.setTitle("Touch");
        stage.getIcons().add(new Image(requireNonNull(getClass().getResourceAsStream("touch.png"))));
        stage.setScene(new Scene(MainView.create()));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
