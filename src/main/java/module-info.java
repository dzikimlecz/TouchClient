module me.dzikimlecz.touchclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires javafx.graphics;
    requires org.jetbrains.annotations;
//    requires dotenv.java;

    exports me.dzikimlecz.touchclient;
    opens me.dzikimlecz.touchclient.mainview to javafx.fxml;
}
