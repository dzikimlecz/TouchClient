package me.dzikimlecz.touchclient.mainview;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainView {



    @NotNull
    public static Parent create() throws Exception {
        return FXMLLoader.load(Objects.requireNonNull(MainView.class.getResource("main-view.fxml")));
    }
}
