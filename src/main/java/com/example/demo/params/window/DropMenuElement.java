package com.example.demo.params.window;

import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DropMenuElement {

    private final GridPane gridPane;
    private final TextInputControl textInputControl1;
    private final TextInputControl textInputControl2;

}
