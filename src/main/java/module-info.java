module com.ioc.pacman {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.poi;
    requires fx.onscreen.keyboard;
    requires jinput;
    requires java.desktop;

    opens com.ioc.pacman to javafx.fxml;
    exports com.ioc.pacman;
}