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
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        System.out.println("Screen bounds: " + bounds);
        System.out.println("Visual bounds: " + visualBounds);

        double minSizeByRows = (bounds.getHeight() - 2 * padding - (rows - 1) * vGap - 40) / rows;
        double minSizeByColumns = (bounds.getWidth() - 2 * padding - (columns - 1) * hGap - 40) / columns;

        double size = Math.min(minSizeByRows, minSizeByColumns);

        System.out.println("minSizeByRows: " + minSizeByRows);
        System.out.println("minSizeByColumns: " + minSizeByColumns);
        System.out.println("size: " + size);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                StackPane stackPane = (i + j) % 2 == 0 ? createGeneratorPane(size) : createTransformerPane(size);
                GridPane.setConstraints(stackPane, j, i);
                gridPane.getChildren().add(stackPane);
            }
        }


//        NodeMatrix matrix = createMatrix(xCord, yCord);
//        matrix.toNodeMetaList().forEach(nm -> GridPane.setConstraints(nm.getShape(), nm.getY(), nm.getX()));
//        gridPane.getChildren().addAll(matrix.toNodeList());

//        StackPane stackPane1 = createTransformerPane(300d);
//        StackPane stackPane2 = createGeneratorPane(300d);
//
//        GridPane.setConstraints(stackPane1, 0, 0);
//        GridPane.setConstraints(stackPane2, 0, 1);
//
//        gridPane.getChildren().add(stackPane1);
//        gridPane.getChildren().add(stackPane2);

        root.getChildren().add(gridPane);
        stage.setTitle("Stupid spiral");
        stage.getIcons().add(new Image("C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\com\\example\\demo\\icons8-spiral-100.png"));
        stage.setScene(scene);

        stage.show();






//        Bounds layoutBounds1 = stackPane1.getLayoutBounds();
//        Bounds boundsInLocal1 = stackPane1.getBoundsInLocal();
//        Bounds boundsInParent1 = stackPane1.getBoundsInParent();
//        System.out.println(layoutBounds1);
//        System.out.println(boundsInLocal1);
//        System.out.println(boundsInParent1);
//        System.out.println();
//        Bounds layoutBounds2 = stackPane2.getLayoutBounds();
//        Bounds boundsInLocal2 = stackPane2.getBoundsInLocal();
//        Bounds boundsInParent2 = stackPane2.getBoundsInParent();
//        System.out.println(layoutBounds2);
//        System.out.println(boundsInLocal2);
//        System.out.println(boundsInParent2);
//
//        System.out.println("scene height" + scene.getHeight());
//        System.out.println("scene width" + scene.getWidth());
//
//        double x1 = stackPane1.getBoundsInParent().getCenterX();
//        double y1 = stackPane1.getBoundsInParent().getCenterY();
//        double x2 = stackPane2.getBoundsInParent().getCenterX();
//        double y2 = stackPane2.getBoundsInParent().getCenterY();
//
//        System.out.println("x1 = " + x1);
//        System.out.println("y1 = " + y1);
//        System.out.println("x2 = " + x2);
//        System.out.println("y2 = " + y2);


//        Line line = new Line();
//        line.setStartX(x1);
//        line.setStartY(y1);
//        line.setEndX(x2);
//        line.setEndY(y2);
//        line.setStrokeWidth(5);
//        line.setStroke(Color.WHITE);

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


//        Thread thread = new Thread(() -> {
//            RuleService service = new RuleService(matrix);
//
//            // Можно запихать этот код в RuleService и плюс передать в качестве параметра функцию колбэк, которая из мэйна будет менять цвет или делать что-то ещё
//            Random random = new Random();
//            for (int x = 0; x < xCord; x++) {
//                for (int y = 0; y < yCord; y++) {
//
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                    NodeMeta meta = matrix.getNodeMeta(x, y).get();
//                    Set<SurfaceType> values = Set.of(SurfaceType.values());
//                    values.forEach(v -> v.setCurrentProb(random.nextInt(100)));
//                    SurfaceType type = null;
//
//                    if (x == 0 && y == 0) {
//                        type = values.stream().max(Comparator.comparingDouble(SurfaceType::getCurrentProb)).get();
//                    } else {
//                        Set<SurfaceType> areaSurface = service.getAreaSurface(meta);
//
//                        if (areaSurface.contains(LAND) && areaSurface.size() == 1) {
//                            // Нужно проверить, что на клетке справа, сверху расположен не берег, который не касается Воды
//                            Optional<NodeMeta> topNode = matrix.getTopNode(meta);
//                            if (topNode.isPresent() && matrix.getRightNode(topNode.get()).filter(m -> SHORE.equals(m.getSurface())).isPresent()) {
//                                Set<SurfaceType> areaSurface1 = service.getAreaSurface(matrix.getRightNode(topNode.get()).get());
//                                if (!areaSurface1.contains(SEA)) {
//                                    // Устанавливаем в текущую клетку БЕРЕГ
//                                    type = SHORE;
//                                } else {
//                                    type = Double.compare(LAND.getCurrentProb() * 1.2, SHORE.getCurrentProb()) > 0 ? LAND : SHORE;
//                                }
//                            } else {
//                                type = Double.compare(LAND.getCurrentProb() * 1.2, SHORE.getCurrentProb()) > 0 ? LAND : SHORE;
//                            }
//                        } else if (areaSurface.contains(LAND) && areaSurface.contains(SHORE)) {
//                            NodeMeta topNode = matrix.getTopNode(meta).get();
//                            if (SHORE.equals(topNode.getSurface())){
//                                if (service.isShoreValid(topNode)) {
//                                    type = Double.compare(LAND.getCurrentProb(), SHORE.getCurrentProb() * 1.2) > 0 ? LAND : SHORE;
//                                } else {
//                                    type = service.getShoreMissingTile(topNode);
//                                }
//                            } else {
//                                type = Double.compare(LAND.getCurrentProb(), SHORE.getCurrentProb() * 1.2) > 0 ? LAND : SHORE;
//                            }
//
//                        } else if (areaSurface.contains(SHORE) && areaSurface.size() == 1) {
//                            // Выбираем поверхность в зависимости от того с чем берега ещё не связаны
//                            // Клетка сверху всегда будет существовать, если мы попали в этот блок
//                            Optional<NodeMeta> topNodeOpt = matrix.getTopNode(meta);
//                            Optional<NodeMeta> leftNodeOpt = matrix.getLeftNode(meta);
//
//                            if (topNodeOpt.isPresent()) {
//                                NodeMeta top = topNodeOpt.get();
//                                if (service.isShoreValid(top)) {
//                                    if (leftNodeOpt.isPresent() && !service.isShoreValid(leftNodeOpt.get())){
//                                        type = service.getShoreMissingTile(leftNodeOpt.get());
//                                    } else {
//                                        type = Double.compare(SEA.getCurrentProb(), LAND.getCurrentProb()) > 0 ? SEA : LAND;
//                                    }
//                                } else {
//                                    type = service.getShoreMissingTile(top);
//                                }
//                            } else {
//                                if (leftNodeOpt.isPresent() && !service.isShoreValid(leftNodeOpt.get())){
//                                    type = service.getShoreMissingTile(leftNodeOpt.get());
//                                } else {
//                                    type = Double.compare(SEA.getCurrentProb(), LAND.getCurrentProb()) > 0 ? SEA : LAND;
//                                }
//                            }
//
//                        } else if (areaSurface.contains(SEA) && areaSurface.contains(SHORE)) {
//                            NodeMeta topNode = matrix.getTopNode(meta).get();
//                            if (SHORE.equals(topNode.getSurface())){
//                                if (service.isShoreValid(topNode)) {
//                                    type = Double.compare(SEA.getCurrentProb(), SHORE.getCurrentProb() * 1.2) > 0 ? SEA : SHORE;
//                                } else {
//                                    type = service.getShoreMissingTile(topNode);
//                                }
//                            } else {
//                                type = Double.compare(SEA.getCurrentProb(), SHORE.getCurrentProb() * 1.2) > 0 ? SEA : SHORE;
//                            }
//
//                        } else if (areaSurface.contains(SEA) && areaSurface.size() == 1) {
//                            // Нужно проверить, что на клетке справа, сверху расположен не берег, который не касается Земли
//                            Optional<NodeMeta> topNode = matrix.getTopNode(meta);
//                            if (topNode.isPresent() && matrix.getRightNode(topNode.get()).filter(m -> SHORE.equals(m.getSurface())).isPresent()) {
//                                Set<SurfaceType> areaSurface1 = service.getAreaSurface(matrix.getRightNode(topNode.get()).get());
//                                if (!areaSurface1.contains(LAND)) {
//                                    // Устанавливаем в текущую клетку БЕРЕГ
//                                    type = SHORE;
//                                } else {
//                                    type = Double.compare(SEA.getCurrentProb() * 1.2, SHORE.getCurrentProb()) > 0 ? SEA : SHORE;
//                                }
//                            } else {
//                                type = Double.compare(SEA.getCurrentProb() * 1.2, SHORE.getCurrentProb()) > 0 ? SEA : SHORE;
//                            }
//                        } else if (areaSurface.contains(SEA) && areaSurface.contains(LAND)) {
//                            type = SHORE;
//                        }
//                    }
//                    setSurfaceToNode(meta, type);
//                }
//            }
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



        // Width/Height = 300 p = value

        //Circle1 : radius = 70 = value * 70 / 300
        // strokeWidth = 8
        // translateX = -35

        //Circle2 : radius = 70
        // strokeWidth = 8
        // translateX = -35

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


    //        Text text = new Text();
//        text.setText("Some text");
//        text.setX(50);
//        text.setY(50);
//        text.setFont(Font.font("Verdana", 50));
//        text.setFill(Color.ROSYBROWN);
//
//        Line line = new Line();
//        line.setStartX(100);
//        line.setStartY(100);
//        line.setEndX(200);
//        line.setEndY(200);
//        line.setStrokeWidth(10);
//        line.setStroke(Color.BLACK);
//        line.setOpacity(0.5);
//        line.setRotate(90);


//        root.getChildren().add(text);
//        root.getChildren().add(line);
}