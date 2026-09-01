module com.practice.moukiladev {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.practice.moukiladev to javafx.fxml;
    exports com.practice.moukiladev;
    exports com.practice.moukiladev.controllers;
    opens com.practice.moukiladev.controllers to javafx.fxml;
}