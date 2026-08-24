module com.moukiladev.Javadb {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.moukiladev.Javadb to javafx.fxml;
    exports com.moukiladev.Javadb;
    exports com.moukiladev.Javadb.controllers;
    opens com.moukiladev.Javadb.controllers to javafx.fxml;
}