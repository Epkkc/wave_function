package com.example.demo.deserealisation;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;

public class TestMain extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();


        Group sceneRoot = new Group();

        javafx.scene.control.ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefViewportHeight(maximumWindowBounds.getHeight() - 38);
        scrollPane.setPrefViewportWidth(maximumWindowBounds.getWidth() - 15);

        sceneRoot.getChildren().add(scrollPane);

        Scene scene = new Scene(sceneRoot, Color.WHITE); // 969faf
        scene.setFill(Color.WHITE);

        stage.setTitle("Wavefunction Collapse Algorithm");
        stage.getIcons().add(new Image("C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\com\\example\\demo\\icon.png"));
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
