package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
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
        double vGap = 5d;
        double hGap = 5d;

        Group root = new Group();
        Scene scene = new Scene(root, Paint.valueOf("#969faf"));

        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(false);
        gridPane.setPadding(new Insets(padding));
        gridPane.setVgap(vGap);
        gridPane.setHgap(hGap);

        int rows = 10;
        int columns = 20;

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        double minSizeByRows = (bounds.getHeight() - 2 * padding - (rows - 1) * vGap - 40) / rows;
        double minSizeByColumns = (bounds.getWidth() - 2 * padding - (columns - 1) * hGap - 40) / columns;

        double size = Math.min(minSizeByRows, minSizeByColumns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                StackPane stackPane = (i + j) % 2 == 0 ? createGeneratorPane(size) : createTransformerPane(size);
                GridPane.setConstraints(stackPane, j, i);
                gridPane.getChildren().add(stackPane);
            }
        }

        root.getChildren().add(gridPane);
        stage.setTitle("Stupid spiral");
        stage.getIcons().add(new Image("C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\com\\example\\demo\\icons8-spiral-100.png"));
        stage.setScene(scene);

        stage.show();

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

    public static NodeMatrix createMatrix(int x, int y) {
        NodeMatrix matrix = new NodeMatrix(x, y);
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                matrix.add(new NodeMeta(createRectangle(), i, j));
            }
        }
        return matrix;
    }

    public static void setSurfaceToNode(NodeMeta meta, SurfaceType type) {
        meta.setSurface(type);
        meta.getShape().setFill(type.getColor());
    }

    public static Rectangle createRectangle() {
        Rectangle recrangle = new Rectangle();
        recrangle.setWidth(300);
        recrangle.setHeight(300);
//        recrangle.setFill(Paint.valueOf("#696969"));
        recrangle.setFill(Paint.valueOf("#7a7a7a"));
        recrangle.setStroke(Color.TRANSPARENT);
        recrangle.setStrokeWidth(1);
//        recrangle.setArcHeight(30);
//        recrangle.setArcWidth(30);
        return recrangle;
    }

    public static StackPane createTransformerPane(Double value) {
        StackPane stackPane = new StackPane();

        Rectangle recrangle = new Rectangle();
        recrangle.setWidth(value);
        recrangle.setHeight(value);
        recrangle.setFill(Paint.valueOf("#363636"));
        recrangle.setStroke(Color.TRANSPARENT);
        recrangle.setStrokeWidth(0);

        Circle circle1 = new Circle();
        circle1.setRadius(value * 7 / 30);
        circle1.setFill(Color.TRANSPARENT);
        circle1.setStroke(Color.WHITE);
        circle1.setStrokeWidth(value * 8 / 300);
        circle1.setTranslateX(-value * 35 / 300);

        Circle circle2 = new Circle();
        circle2.setRadius(value * 7 / 30);
        circle2.setFill(Color.TRANSPARENT);
        circle2.setStroke(Color.WHITE);
        circle2.setStrokeWidth(value * 8 / 300);
        circle2.setTranslateX(value * 35 / 300);

        stackPane.getChildren().add(recrangle);
        stackPane.getChildren().add(circle1);
        stackPane.getChildren().add(circle2);

        return stackPane;
    }

    public static StackPane createGeneratorPane(Double value) {
        StackPane stackPane = new StackPane();

        Rectangle recrangle = new Rectangle();
        recrangle.setWidth(value);
        recrangle.setHeight(value);
        recrangle.setFill(Paint.valueOf("#363636"));
        recrangle.setStroke(Color.TRANSPARENT);
        recrangle.setStrokeWidth(1);

        Circle circle1 = new Circle();
        circle1.setRadius(value * 9 / 30);
        circle1.setFill(Color.TRANSPARENT);
        circle1.setStroke(Color.WHITE);
        circle1.setStrokeWidth(value * 8 / 300);

        double radius = value / 10;
        double width = value / 50;

        Path path1 = drawSemiRing(0, 0, radius, radius - width, Color.WHITE, Color.WHITE, true);
        path1.setTranslateX(-(radius - width / 2));
        path1.setTranslateY(radius / 2);

        Path path2 = drawSemiRing(0, 0, radius, radius - width, Color.WHITE, Color.WHITE, false);
        path2.setTranslateX(radius - width / 2);
        path2.setTranslateY(-(radius / 2));

        stackPane.getChildren().add(recrangle);
        stackPane.getChildren().add(circle1);
        stackPane.getChildren().add(path1);
        stackPane.getChildren().add(path2);

        return stackPane;
    }

    public static Path drawSemiRing(double centerX, double centerY, double radius, double innerRadius, Color bgColor, Color strkColor, boolean sweepFlag) {
        Path path = new Path();
        path.setFill(bgColor);
        path.setStroke(strkColor);
        path.setFillRule(FillRule.EVEN_ODD);

        MoveTo moveTo = new MoveTo();
        moveTo.setX(centerX + innerRadius);
        moveTo.setY(centerY);

        ArcTo arcToInner = new ArcTo();
        arcToInner.setX(centerX - innerRadius);
        arcToInner.setY(centerY);
        arcToInner.setRadiusX(innerRadius);
        arcToInner.setRadiusY(innerRadius);
        arcToInner.setSweepFlag(sweepFlag);

        MoveTo moveTo2 = new MoveTo();
        moveTo2.setX(centerX + innerRadius);
        moveTo2.setY(centerY);

        HLineTo hLineToRightLeg = new HLineTo();
        hLineToRightLeg.setX(centerX + radius);

        ArcTo arcTo = new ArcTo();
        arcTo.setX(centerX - radius);
        arcTo.setY(centerY);
        arcTo.setRadiusX(radius);
        arcTo.setRadiusY(radius);
        arcTo.setSweepFlag(sweepFlag);

        HLineTo hLineToLeftLeg = new HLineTo();
        hLineToLeftLeg.setX(centerX - innerRadius);

        path.getElements().add(moveTo);
        path.getElements().add(arcToInner);
        path.getElements().add(moveTo2);
        path.getElements().add(hLineToRightLeg);
        path.getElements().add(arcTo);
        path.getElements().add(hLineToLeftLeg);

        return path;
    }

}