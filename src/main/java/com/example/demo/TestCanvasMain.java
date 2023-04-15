//package com.example.demo;
//
//import javafx.application.Application;
//import javafx.scene.Group;
//import javafx.scene.Scene;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.paint.Color;
//import javafx.scene.paint.Paint;
//import javafx.stage.Stage;
//
//public class TestCanvasMain extends Application {
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        primaryStage.setTitle("Drawing Operations Test");
//        Group root = new Group();
//        Canvas canvas = new Canvas(300, 1000);
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//
//        hook(gc);
//
//        root.getChildren().add(canvas);
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();
//    }
//
//    private void hook(GraphicsContext gc) {
//        gc.setFill(Color.BLANCHEDALMOND);
//        gc.
//    }
//}
//
