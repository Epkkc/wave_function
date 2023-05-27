package com.example.demo.java.fx.model.visual;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import lombok.Data;

@Data
public class BasePane {

    private StackPane stackPane;
    private StatusPane statusPane;

    public BasePane(double size) {

        stackPane = new StackPane();

        statusPane = createStatusPane(size);

        Rectangle rectangle = createRectangle(size);

        stackPane.getChildren().add(rectangle);
        stackPane.getChildren().add(statusPane.getHbox());
    }

    private Rectangle createRectangle(double size) {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(size);
        rectangle.setHeight(size);
        rectangle.setFill(Paint.valueOf("#F6FAFB")); // F6FAFB // F5F9FA // F4F8F9 // #363636 // F0FFFF // e7e7e7 // FFFFF0 // #F5F5F5 - ЭТО ЦВЕТ БЭКГРАУНДА ОКНА ВИНДОУС // #F8F8F8
        rectangle.setStroke(Color.TRANSPARENT);
        rectangle.setStrokeWidth(0);

        return rectangle;
    }

    private StatusPane createStatusPane(double size) {
        return new StatusPane(size);
    }


}
