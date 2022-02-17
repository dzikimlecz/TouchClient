package me.dzikimlecz.touchclient.mainview;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import me.dzikimlecz.touchclient.TouchApp;
import me.dzikimlecz.touchclient.model.UserProfile;

import static me.dzikimlecz.touchclient.model.UserProfile.getUsername;
import static me.dzikimlecz.touchclient.model.UserProfile.parseTag;

public class LoginView {
    @FXML
    public TextField usernameField;
    private TouchApp app;

    @FXML
    protected void onApproved(@SuppressWarnings("unused") ActionEvent __) {
        final var strings = usernameField.getText().split("#");
        final var profile = UserProfile.of(getUsername(strings), parseTag(strings[1]));
        app.login(profile);
    }

    public void setApp(TouchApp app) {
        this.app = app;
    }
}
