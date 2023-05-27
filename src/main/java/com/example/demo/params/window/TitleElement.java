package com.example.demo.params.window;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TitleElement {

    private final StackPane stackPane;
    private final Label text;
    private final Line bottomLine;
    private final String titleText;
    private final int stackPanePadding;

    public void disableTitle(boolean isDropMenuShown) {
        text.setTextFill(Color.GRAY);
//        stackPane.setBorder(
//            new Border(
//                new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color.LIGHTGRAY, Color.TRANSPARENT,
//                    BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
//                    CornerRadii.EMPTY, new BorderWidths(2), new Insets(0))
//            )
//        );

        if (isDropMenuShown) {
            bottomLine.endXProperty().unbind();
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, new KeyValue(bottomLine.endXProperty(), stackPane.getWidth() - 3 * stackPanePadding)));
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(bottomLine.endXProperty(), text.getWidth())));
            timeline.play();

            timeline.setOnFinished(actionEvent -> {
//                bottomLine.endXProperty().bind(stackPane.widthProperty().add(-3*stackPanePadding));
                bottomLine.endXProperty().bind(text.widthProperty());
                bottomLine.setStroke(Color.LIGHTGRAY);
                stackPane.setDisable(true);
            });
        } else {
            bottomLine.setStroke(Color.LIGHTGRAY);
            stackPane.setDisable(true);
        }


//        stackPane.setDisable(true);
    }

    public void enableTitle() {
        text.setTextFill(Color.BLACK);
//        stackPane.setBorder(
//            new Border(
//                new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Paint.valueOf("#007acc"), Color.TRANSPARENT,
//                    BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
//                    CornerRadii.EMPTY, new BorderWidths(2), new Insets(0))
//            )
//        );


//        bottomLine.endXProperty().unbind();
//        Timeline timeline = new Timeline();
//        timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, new KeyValue(bottomLine.endXProperty(), stackPane.getWidth() - 3 * stackPanePadding)));
//        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(bottomLine.endXProperty(), text.getWidth())));
//        timeline.play();
//
//        timeline.setOnFinished(actionEvent -> {
//            bottomLine.endXProperty().bind(text.widthProperty());
//            stackPane.setDisable(false);
//        });
        bottomLine.setStroke(Paint.valueOf("#007acc"));
        stackPane.setDisable(false);
    }

    public void expandBottomLine() {
        bottomLine.endXProperty().unbind();
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, new KeyValue(bottomLine.endXProperty(), text.getWidth())));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(bottomLine.endXProperty(), stackPane.getWidth() - 3 * stackPanePadding)));
        timeline.play();

        timeline.setOnFinished(actionEvent -> {
            bottomLine.endXProperty().bind(stackPane.widthProperty().add(-3*stackPanePadding));
        });
    }

    public void collapseBottomLine() {
        bottomLine.endXProperty().unbind();
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, new KeyValue(bottomLine.endXProperty(), stackPane.getWidth() - 3 * stackPanePadding)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(bottomLine.endXProperty(), text.getWidth())));
        timeline.play();

        timeline.setOnFinished(actionEvent -> {
            bottomLine.endXProperty().bind(text.widthProperty());
        });
    }

}
