package me.dzikimlecz.touchclient.mainview;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class MainView {
    @NotNull
    public static Parent create() throws Exception {
        return FXMLLoader.load(requireNonNull(MainView.class.getResource("main-view.fxml")));
    }
}
