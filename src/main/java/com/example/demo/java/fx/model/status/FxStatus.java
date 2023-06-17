package com.example.demo.java.fx.model.status;

import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.model.status.StatusMetaDto;
import com.example.demo.base.model.status.StatusType;
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
import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.StringJoiner;

@Data
@EqualsAndHashCode(callSuper = true)
public class FxStatus extends BaseStatus {

    private Shape shape;
    private final StringJoiner tooltipMessage;


    public FxStatus(StatusType statusType, Collection<StatusMetaDto> statusDtos, double size) {
        super(statusType, statusDtos);
        this.shape = getStatusForm(size, statusType);
        this.tooltipMessage = new StringJoiner(", ", statusType.getTooltipPrefix() + "\n", "");
    }

    private Shape getStatusForm(double size, StatusType type) {

        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(size);
        rectangle.setHeight(size);
        rectangle.setFill(type.getColor());
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
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(type.getTooltipPrefix());
        joiner.add("Уровни напряжения:");
        voltageLevelChainLinkHashMap.forEach((voltageLevel, dto) -> {
            joiner.add("Уровень напряжения: " + voltageLevel.getDescription());
            joiner.add("Номер звена: " + dto.getChainLinkOrder());
            joiner.add("Uuid ноды: " + dto.getNodeUuid());
        });
        return joiner.toString();
    }

}
