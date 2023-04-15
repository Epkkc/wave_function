package com.example.demo.model.status;

import com.example.demo.model.power.node.Coordinates;
import com.example.demo.model.power.node.VoltageLevel;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.StringJoiner;

@Data
@SuperBuilder
public class Status extends StatusMeta implements Coordinates {

    private int x;
    private int y;
    private Shape shape;
    private final StringJoiner tooltipMessage;


    public Status(StatusType type, int x, int y, double size, VoltageLevel... voltageLevels) {
        super(type, voltageLevels);
        this.x = x;
        this.y = y;
        this.shape = getStatusForm(size, type);
        addVoltageLevel(voltageLevels);
        this.tooltipMessage = new StringJoiner(", ", type.getTooltipPrefix() + "\n", "");
    }

    private Shape getStatusForm(double size, StatusType type) {

        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(size);
        rectangle.setHeight(size);
        rectangle.setFill(type.getColor()); // #363636
        rectangle.setStroke(Color.TRANSPARENT);
        rectangle.setStrokeWidth(0);


        Text text = new Text();
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.getDefault());

        StackPane stickyNotesPane = new StackPane();
        stickyNotesPane.setPadding(new Insets(2));
        stickyNotesPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85);");
        stickyNotesPane.getChildren().add(text);

        Popup popup = new Popup();
        popup.getContent().add(stickyNotesPane);


        rectangle.hoverProperty().addListener((obs, oldVal, newValue) -> {
            if (newValue) {
                Bounds bnds = rectangle.localToScreen(rectangle.getLayoutBounds());
                double x = bnds.getMinX() - (stickyNotesPane.getWidth() / 2) + (rectangle.getWidth() / 2);
                double y = bnds.getMinY() - stickyNotesPane.getHeight() - 5;
                rectangle.setOpacity(0.7); // Меняем прозрачноть(цвет) элемента, на который навели мышью

                text.setText(createTooltipMessage());
                popup.show(rectangle, x, y);
            } else {
                rectangle.setOpacity(1); // Возвращаем дефолтную прозрачность 1
                popup.hide();
            }
        });

        return rectangle;
    }

    private String createTooltipMessage() {
        StringJoiner joiner = new StringJoiner(", ", type.getTooltipPrefix() + "\n", "");
        voltageLevels.forEach(level -> joiner.add(level.getDescription()));
        return joiner.toString();
    }

}
