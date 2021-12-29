package me.dzikimlecz.touchclient.mainview;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import me.dzikimlecz.touchclient.model.Message;
import me.dzikimlecz.touchclient.model.message.MessageDisplayMode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static me.dzikimlecz.touchclient.model.message.MessageDisplayMode.NULL;

public class MessageContainer  {
    @FXML
    public BorderPane root;
    private final Label label = new Label();



    @Contract(mutates = "this")
    public void put(@NotNull Message msg, @NotNull MessageDisplayMode displayMode) {
        label.setStyle("-fx-padding: 3px;");
        root.getChildren().clear();
        label.setText(displayMode != NULL ? msg.getContent() : " ");
        switch (displayMode) {
            case AS_RECIPIENT:
                setBackground(label, Color.POWDERBLUE);
                root.setLeft(label);
                break;
            case AS_SENDER:
                setBackground(label, Color.BLANCHEDALMOND);
                root.setRight(label);
                break;
            case NULL:
                setBackground(label, Color.WHITESMOKE);
                root.setCenter(label);
                break;
        }
    }

    private static void setBackground(Region node, Paint fill) {
        node.setBackground(new Background(new BackgroundFill(fill, new CornerRadii(4), Insets.EMPTY)));
    }
}
