module BlackBox {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.base;
    requires java.desktop;

    opens com.example.matformater to javafx.graphics;
    opens com.example.matformater.controller to javafx.fxml;
}