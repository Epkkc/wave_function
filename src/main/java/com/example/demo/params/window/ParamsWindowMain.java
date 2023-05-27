package com.example.demo.params.window;

import com.example.demo.params.window.elements.Switch;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ParamsWindowMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        java.awt.Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        Group sceneRoot = new Group();
        sceneRoot.prefHeight(maximumWindowBounds.getHeight());
        sceneRoot.prefWidth(maximumWindowBounds.getWidth());

        sceneRoot.getChildren().add(getCheckBox());

        Scene scene = new Scene(sceneRoot, Color.WHITE);
        scene.setFill(Color.WHITE);

        String testCss = getClass().getResource("/css/switch/switch.css").toExternalForm();
        scene.getStylesheets().add(testCss);

        stage.setTitle("Wavefunction Collapse Algorithm");
        stage.getIcons().add(new Image("C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\com\\example\\demo\\icon.png"));
        stage.setScene(scene);
        stage.setMaximized(true);

        stage.show();


    }

    private StackPane getCheckBox() {
        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(20));

        Switch switchControl = new Switch("Some Text");
        stackPane.getChildren().add(switchControl);

        return stackPane;
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }
}
