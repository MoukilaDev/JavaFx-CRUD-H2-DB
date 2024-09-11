module com.moukiladev.javadb {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.moukiladev.javadb to javafx.fxml;
    exports com.moukiladev.javadb;
    exports com.moukiladev.javadb.controllers;
    opens com.moukiladev.javadb.controllers to javafx.fxml;
}