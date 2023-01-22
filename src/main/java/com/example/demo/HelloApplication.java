package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static com.example.demo.SurfaceType.LAND;
import static com.example.demo.SurfaceType.SEA;
import static com.example.demo.SurfaceType.SHORE;

public class HelloApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        Group root = new Group();
        Scene scene = new Scene(root, Paint.valueOf("#363636"));

        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(false);
        gridPane.setPadding(new Insets(20));
        gridPane.setVgap(3);
        gridPane.setHgap(3);

        int xCord = 7;
        int yCord = 11;

        NodeMatrix matrix = createMatrix(xCord, yCord);

        matrix.toNodeMetaList().forEach(nm -> GridPane.setConstraints(nm.getShape(), nm.getY(), nm.getX()));

        gridPane.getChildren().addAll(matrix.toNodeList());

        root.getChildren().add(gridPane);

        stage.setTitle("Stupid spiral");
        stage.getIcons().add(new Image("C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\com\\example\\demo\\icons8-spiral-100.png"));
        stage.setScene(scene);
        stage.show();

        Thread thread = new Thread(() -> {
            RuleService service = new RuleService(matrix);

            // Можно запихать этот код в RuleService и плюс передать в качестве параметра функцию колбэк, которая из мэйна будет менять цвет или делать что-то ещё
            Random random = new Random();
            for (int x = 0; x < xCord; x++) {
                for (int y = 0; y < yCord; y++) {

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    NodeMeta meta = matrix.getNodeMeta(x, y).get();
                    Set<SurfaceType> values = Set.of(SurfaceType.values());
                    values.forEach(v -> v.setCurrentProb(random.nextInt(100)));
                    SurfaceType type = null;

                    if (x == 0 && y == 0) {
                        type = values.stream().max(Comparator.comparingDouble(SurfaceType::getCurrentProb)).get();
                    } else {
                        Set<SurfaceType> areaSurface = service.getAreaSurface(meta);

                        if (areaSurface.contains(LAND) && areaSurface.size() == 1) {
                            // Нужно проверить, что на клетке справа, сверху расположен не берег, который не касается Воды
                            Optional<NodeMeta> topNode = matrix.getTopNode(meta);
                            if (topNode.isPresent() && matrix.getRightNode(topNode.get()).filter(m -> SHORE.equals(m.getSurface())).isPresent()) {
                                Set<SurfaceType> areaSurface1 = service.getAreaSurface(matrix.getRightNode(topNode.get()).get());
                                if (!areaSurface1.contains(SEA)) {
                                    // Устанавливаем в текущую клетку БЕРЕГ
                                    type = SHORE;
                                } else {
                                    type = Double.compare(LAND.getCurrentProb() * 1.2, SHORE.getCurrentProb()) > 0 ? LAND : SHORE;
                                }
                            } else {
                                type = Double.compare(LAND.getCurrentProb() * 1.2, SHORE.getCurrentProb()) > 0 ? LAND : SHORE;
                            }
                        } else if (areaSurface.contains(LAND) && areaSurface.contains(SHORE)) {
                            NodeMeta topNode = matrix.getTopNode(meta).get();
                            if (SHORE.equals(topNode.getSurface())){
                                if (service.isShoreValid(topNode)) {
                                    type = Double.compare(LAND.getCurrentProb(), SHORE.getCurrentProb() * 1.2) > 0 ? LAND : SHORE;
                                } else {
                                    type = service.getShoreMissingTile(topNode);
                                }
                            } else {
                                type = Double.compare(LAND.getCurrentProb(), SHORE.getCurrentProb() * 1.2) > 0 ? LAND : SHORE;
                            }

                        } else if (areaSurface.contains(SHORE) && areaSurface.size() == 1) {
                            // Выбираем поверхность в зависимости от того с чем берега ещё не связаны
                            // Клетка сверху всегда будет существовать, если мы попали в этот блок
                            Optional<NodeMeta> topNodeOpt = matrix.getTopNode(meta);
                            Optional<NodeMeta> leftNodeOpt = matrix.getLeftNode(meta);

                            if (topNodeOpt.isPresent()) {
                                NodeMeta top = topNodeOpt.get();
                                if (service.isShoreValid(top)) {
                                    if (leftNodeOpt.isPresent() && !service.isShoreValid(leftNodeOpt.get())){
                                        type = service.getShoreMissingTile(leftNodeOpt.get());
                                    } else {
                                        type = Double.compare(SEA.getCurrentProb(), LAND.getCurrentProb()) > 0 ? SEA : LAND;
                                    }
                                } else {
                                    type = service.getShoreMissingTile(top);
                                }
                            } else {
                                if (leftNodeOpt.isPresent() && !service.isShoreValid(leftNodeOpt.get())){
                                    type = service.getShoreMissingTile(leftNodeOpt.get());
                                } else {
                                    type = Double.compare(SEA.getCurrentProb(), LAND.getCurrentProb()) > 0 ? SEA : LAND;
                                }
                            }

                        } else if (areaSurface.contains(SEA) && areaSurface.contains(SHORE)) {
                            NodeMeta topNode = matrix.getTopNode(meta).get();
                            if (SHORE.equals(topNode.getSurface())){
                                if (service.isShoreValid(topNode)) {
                                    type = Double.compare(SEA.getCurrentProb(), SHORE.getCurrentProb() * 1.2) > 0 ? SEA : SHORE;
                                } else {
                                    type = service.getShoreMissingTile(topNode);
                                }
                            } else {
                                type = Double.compare(SEA.getCurrentProb(), SHORE.getCurrentProb() * 1.2) > 0 ? SEA : SHORE;
                            }

                        } else if (areaSurface.contains(SEA) && areaSurface.size() == 1) {
                            // Нужно проверить, что на клетке справа, сверху расположен не берег, который не касается Земли
                            Optional<NodeMeta> topNode = matrix.getTopNode(meta);
                            if (topNode.isPresent() && matrix.getRightNode(topNode.get()).filter(m -> SHORE.equals(m.getSurface())).isPresent()) {
                                Set<SurfaceType> areaSurface1 = service.getAreaSurface(matrix.getRightNode(topNode.get()).get());
                                if (!areaSurface1.contains(LAND)) {
                                    // Устанавливаем в текущую клетку БЕРЕГ
                                    type = SHORE;
                                } else {
                                    type = Double.compare(SEA.getCurrentProb() * 1.2, SHORE.getCurrentProb()) > 0 ? SEA : SHORE;
                                }
                            } else {
                                type = Double.compare(SEA.getCurrentProb() * 1.2, SHORE.getCurrentProb()) > 0 ? SEA : SHORE;
                            }
                        } else if (areaSurface.contains(SEA) && areaSurface.contains(LAND)) {
                            type = SHORE;
                        }
                    }
                    setSurfaceToNode(meta, type);
                }
            }
        });
        thread.start();




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

    public static Rectangle createRectangle() {
        Rectangle recrangle = new Rectangle();
        recrangle.setWidth(100);
        recrangle.setHeight(100);
//        recrangle.setFill(Paint.valueOf("#696969"));
        recrangle.setFill(Paint.valueOf("#7a7a7a"));
        recrangle.setStroke(Color.TRANSPARENT);
        recrangle.setStrokeWidth(1);
//        recrangle.setArcHeight(30);
//        recrangle.setArcWidth(30);
        return recrangle;
    }

    public static void setSurfaceToNode(NodeMeta meta, SurfaceType type) {
        meta.setSurface(type);
        meta.getShape().setFill(type.getColor());
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