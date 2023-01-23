package com.example.demo;

import com.example.demo.model.Matrix;
import com.example.demo.services.ElementsService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        double padding = 20d;
        double vGap = 2d;
        double hGap = 2d;

        ElementsService elementsService = new ElementsService();

        Group root = new Group();
        Scene scene = new Scene(root, Paint.valueOf("#969faf"));

        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(false);
        gridPane.setPadding(new Insets(padding));
        gridPane.setVgap(vGap);
        gridPane.setHgap(hGap);

        int rows = 10; // 18
        int columns = 10; // 35

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        double minSizeByRows = (bounds.getHeight() - 2 * padding - (rows - 1) * vGap - 50) / rows;
        double minSizeByColumns = (bounds.getWidth() - 2 * padding - (columns - 1) * hGap - 50) / columns;
        double size = Math.min(minSizeByRows, minSizeByColumns);



        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                StackPane stackPane = (i + j) % 2 == 0 ? elementsService.createGeneratorPane(size) : elementsService.createTransformerPane(size);
                GridPane.setConstraints(stackPane, j, i);
                gridPane.getChildren().add(stackPane);
            }
        }

        root.getChildren().add(gridPane);
        stage.setTitle("Wavefunction Collapse Algorithm");
        stage.getIcons().add(new Image("C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\com\\example\\demo\\icon.png"));
        stage.setScene(scene);

        stage.show();

//        Line line = new Line();
//        line.setStartX();
//        line.setStartY();
//        line.setEndX();
//        line.setEndY();

//        Thread thread = new Thread(() -> {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            Platform.runLater(() -> {
//                root.getChildren().add(line);
//            });
//        });
//        thread.start();

    }


//    public static void setSurfaceToNode(NodeMeta meta, SurfaceType type) {
//        meta.setSurface(type);
//        meta.getNode().setFill(type.getColor());
//    }





}