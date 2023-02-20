module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires static lombok;
    exports com.example.demo;
    exports com.example.demo.model;
    opens com.example.demo.model to javafx.fxml;
    exports com.example.demo.services;
    opens com.example.demo.services to javafx.fxml;
    exports com.example.demo.factories;
    opens com.example.demo.factories to javafx.fxml;
    exports com.example.demo.model.power.node;
    opens com.example.demo.model.power.node to javafx.fxml;
    exports com.example.demo.model.status;
    opens com.example.demo.model.status to javafx.fxml;
    exports com.example.demo.utils;
    exports com.example.demo.services.filters;
    opens com.example.demo.services.filters to javafx.fxml;
    exports com.example.demo.model.filter;
    opens com.example.demo.model.filter to javafx.fxml;
    exports com.example.demo.thread;
    opens com.example.demo.thread to javafx.fxml;
}