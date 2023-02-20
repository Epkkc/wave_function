//package com.example.demo.model;
//
//import javafx.scene.layout.StackPane;
//import javafx.scene.paint.Color;
//import javafx.scene.paint.Paint;
//import javafx.scene.shape.ArcTo;
//import javafx.scene.shape.Circle;
//import javafx.scene.shape.FillRule;
//import javafx.scene.shape.HLineTo;
//import javafx.scene.shape.MoveTo;
//import javafx.scene.shape.Path;
//import javafx.scene.shape.Rectangle;
//
//public class Generator extends PowerNode {
//    public Generator(double size) {
//        super(size);
//        setType(PowerNodeType.GENERATOR);
//    }
//
//    @Override
//    public StackPane getVisualStackPane(double size) {
//        StackPane stackPane = new StackPane();
//
//        Rectangle recrangle = new Rectangle();
//        recrangle.setWidth(size);
//        recrangle.setHeight(size);
//        recrangle.setFill(Paint.valueOf("#363636"));
//        recrangle.setStroke(Color.TRANSPARENT);
//        recrangle.setStrokeWidth(1);
//
//        Circle circle1 = new Circle();
//        circle1.setRadius(size * 9 / 30);
//        circle1.setFill(Color.TRANSPARENT);
//        circle1.setStroke(Color.WHITE);
//        circle1.setStrokeWidth(size * 8 / 300);
//
//        double radius = size / 10;
//        double width = size / 50;
//
//        Path path1 = drawSemiRing(0, 0, radius, radius - width, Color.WHITE, Color.WHITE, true);
//        path1.setTranslateX(-(radius - width / 2));
//        path1.setTranslateY(radius / 2);
//
//        Path path2 = drawSemiRing(0, 0, radius, radius - width, Color.WHITE, Color.WHITE, false);
//        path2.setTranslateX(radius - width / 2);
//        path2.setTranslateY(-(radius / 2));
//
//        stackPane.getChildren().add(recrangle);
//        stackPane.getChildren().add(circle1);
//        stackPane.getChildren().add(path1);
//        stackPane.getChildren().add(path2);
//
//        return stackPane;
//    }
//
//    public Path drawSemiRing(double centerX, double centerY, double radius, double innerRadius, Color bgColor, Color strkColor, boolean sweepFlag) {
//        Path path = new Path();
//        path.setFill(bgColor);
//        path.setStroke(strkColor);
//        path.setFillRule(FillRule.EVEN_ODD);
//
//        MoveTo moveTo = new MoveTo();
//        moveTo.setX(centerX + innerRadius);
//        moveTo.setY(centerY);
//
//        ArcTo arcToInner = new ArcTo();
//        arcToInner.setX(centerX - innerRadius);
//        arcToInner.setY(centerY);
//        arcToInner.setRadiusX(innerRadius);
//        arcToInner.setRadiusY(innerRadius);
//        arcToInner.setSweepFlag(sweepFlag);
//
//        MoveTo moveTo2 = new MoveTo();
//        moveTo2.setX(centerX + innerRadius);
//        moveTo2.setY(centerY);
//
//        HLineTo hLineToRightLeg = new HLineTo();
//        hLineToRightLeg.setX(centerX + radius);
//
//        ArcTo arcTo = new ArcTo();
//        arcTo.setX(centerX - radius);
//        arcTo.setY(centerY);
//        arcTo.setRadiusX(radius);
//        arcTo.setRadiusY(radius);
//        arcTo.setSweepFlag(sweepFlag);
//
//        HLineTo hLineToLeftLeg = new HLineTo();
//        hLineToLeftLeg.setX(centerX - innerRadius);
//
//        path.getElements().add(moveTo);
//        path.getElements().add(arcToInner);
//        path.getElements().add(moveTo2);
//        path.getElements().add(hLineToRightLeg);
//        path.getElements().add(arcTo);
//        path.getElements().add(hLineToLeftLeg);
//
//        return path;
//    }
//}
