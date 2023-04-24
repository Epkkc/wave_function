package com.example.demo.java.fx.v2.model;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.java.fx.model.power.FxPowerLine;
import com.example.demo.java.fx.model.power.FxPowerNode;
import com.example.demo.java.fx.service.FxConfiguration;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class GraphicalContext {
    private final FxConfiguration configuration;
    private final Stage stage;
    private final Scene scene;
    private final ScrollPane scrollPane;
    private final Group root;
    private final GridPane gridPane;
    private final Matrix<AbstractPowerNodeFxProxy> matrix;
    private List<FxPowerLine> lines = new ArrayList<>();
}
