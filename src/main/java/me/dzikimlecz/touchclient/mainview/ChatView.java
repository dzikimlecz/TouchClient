package me.dzikimlecz.touchclient.mainview;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import me.dzikimlecz.touchclient.model.UserProfile;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatView implements Initializable {
    @FXML
    public ImageView imageView;
    @FXML
    private Label userNameTag;




    public void setProfile(@NotNull UserProfile profile) {
        userNameTag.setText(profile.getNameTag());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageView.setImage(new Image(getClass().getResourceAsStream("user-profile.png")));
    }
}
