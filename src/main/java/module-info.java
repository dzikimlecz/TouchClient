module me.dzikimlecz.touchclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires javafx.graphics;
    requires org.jetbrains.annotations;
    requires dotenv.java;
    requires coresearch.cvurl.io;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports me.dzikimlecz.touchclient;
    opens me.dzikimlecz.touchclient.mainview to javafx.fxml;
    opens me.dzikimlecz.touchclient.model to  com.fasterxml.jackson.databind;
    opens me.dzikimlecz.touchclient.model.container to com.fasterxml.jackson.databind;
    opens me.dzikimlecz.touchclient.client to com.fasterxml.jackson.databind, javafx.fxml;
}
