module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires static lombok;
    requires org.apache.commons.collections4;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;


    exports com.example.demo.services;
    exports com.example.demo.java.fx.factories;
    exports com.example.demo.utils;
    exports com.example.demo.thread;
    exports com.example.demo.export.dto;
    exports com.example.demo.java.fx.model.power;
    exports com.example.demo.java.fx.model.grid;
    exports com.example.demo.base.model.configuration;
    exports com.example.demo.base.model.power;
    exports com.example.demo.base.model.status;
    exports com.example.demo.base.model.grid;
    exports com.example.demo.base.model.enums;
    exports com.example.demo.java.fx.model.status;
    exports com.example.demo.java.fx.model.visual;
    exports com.example.demo.java.fx.algorithm;
    exports com.example.demo.java.fx.service;
    exports com.example.demo.params.window;


    opens com.example.demo.services to javafx.fxml;
    opens com.example.demo.java.fx.factories to javafx.fxml;
    opens com.example.demo.thread to javafx.fxml;
    opens com.example.demo to javafx.fxml, javafx.graphics;
    opens com.example.demo.java.fx.model.grid to javafx.fxml;
    opens com.example.demo.base.model.configuration to javafx.fxml;
    opens com.example.demo.base.model.power to javafx.fxml;
    opens com.example.demo.base.model.enums to javafx.fxml;
    opens com.example.demo.base.model.grid to com.fasterxml.jackson.databind, javafx.fxml;
    opens com.example.demo.java.fx.model.power to com.fasterxml.jackson.databind, javafx.fxml;
    opens com.example.demo.java.fx.model.status to javafx.fxml;
    opens com.example.demo.java.fx.algorithm to javafx.fxml;
    opens com.example.demo.deserealisation to javafx.fxml, javafx.graphics;
    opens com.example.demo.base to javafx.fxml, javafx.graphics;
    opens com.example.demo.java.fx to javafx.fxml, javafx.graphics;
    opens com.example.demo.java.fx.service to javafx.fxml;
    exports com.example.demo.base.service;
    opens com.example.demo.base.service to javafx.fxml;
    exports com.example.demo.base.service.status;
    opens com.example.demo.base.service.status to javafx.fxml;
    exports com.example.demo.base.service.element;
    opens com.example.demo.base.service.element to javafx.fxml;
    exports com.example.demo.base.service.connection;
    opens com.example.demo.base.service.connection to javafx.fxml;
    opens com.example.demo.base.model.status to javafx.fxml, javafx.graphics;

}