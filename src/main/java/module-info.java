module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires static lombok;
    requires org.apache.commons.collections4;
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
    exports com.example.demo.procedure;
    opens com.example.demo.procedure to javafx.fxml;
    opens com.example.demo to javafx.fxml, javafx.graphics;
}