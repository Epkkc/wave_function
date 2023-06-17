package com.example.demo.params.window;

import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.java.fx.FxMain;
import com.example.demo.params.window.elements.ExtendedControl;
import com.example.demo.params.window.elements.Switch;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class ParamsWindowMainWithVoltage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static GridPane gridPane;
    private static ScrollPane scrollPane;
    final int totalColumns = 12;
    final int totalMenuTableColumns = 12;


    @Override
    public void start(Stage stage) throws Exception {
        prepareVisualComponents(stage);

        VoltageLevel[] voltageLevels = VoltageLevel.values();
        Map<Integer, VoltageLevelInput> transformerInputs = new HashMap<>();
        Map<Integer, VoltageLevelInput> generatorInputs = new HashMap<>();
        Map<Integer, VoltageLevelInput> loadInputs = new HashMap<>();
        int row = 1;

        Label transformerLabel = new Label("Настройки трансформаторов");
        transformerLabel.getStyleClass().add("label1");
        gridPane.add(transformerLabel, 0, 0, 4, 1);

        Label generatorLabel = new Label("Настройки генераторов");
        generatorLabel.getStyleClass().add("label1");
        gridPane.add(generatorLabel, 4, 0, 4, 1);

        Label loadLabel = new Label("Настройки нагрузок");
        loadLabel.getStyleClass().add("label1");
        gridPane.add(loadLabel, 8, 0, 4, 1);

        for (int i = 0; i < voltageLevels.length; i++) {
            VoltageLevelInput transformersInput = preparePaneForVoltageLevel(voltageLevels[i], row, 0,  4);
            VoltageLevelInput generatorsInput = preparePaneForVoltageLevel(voltageLevels[i], row, 4, 4); // todo Заменить на соответствующий класс
            VoltageLevelInput loadInput = preparePaneForVoltageLevel(voltageLevels[i], row, 8, 4); // todo Заменить на соответствующий класс
            transformerInputs.put(voltageLevels[i].getVoltageLevel(), transformersInput);
            generatorInputs.put(voltageLevels[i].getVoltageLevel(), generatorsInput);
            loadInputs.put(voltageLevels[i].getVoltageLevel(), loadInput);

            row = (i + 1) * 2 + 1;
        }

        Label label = addLabel( row + 1, totalColumns);
        FxMain fxMain = new FxMain();
        Button button = addSubmitButton(transformerInputs, label, row, totalColumns);
        button.setOnAction(actionEvent -> {
            fxMain.start(stage);
        });

        stage.show();

    }

    private Label addLabel(int row, int totalColumns) {
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        gridPane.add(label, 0, row, totalColumns, 1);
        return label;
    }

    private Button addSubmitButton(Map<Integer, VoltageLevelInput> transformerInputs, Label label, int row, int totalColumns) {
        Button submitButton = new Button("Submit");
        submitButton.setAlignment(Pos.CENTER);
        submitButton.setStyle("-fx-font-family: Wix Madefor Display; -fx-font-size: 30;");

        submitButton.setOnAction(actionEvent -> {
            label.setText(createMessageFromInputs(transformerInputs));
        });

        gridPane.add(submitButton, 0, row, totalColumns, 1);

        return submitButton;
    }

    private String createMessageFromInputs(Map<Integer, VoltageLevelInput> transformerInputs) {
        StringBuilder sb = new StringBuilder();

        for (VoltageLevelInput input : transformerInputs.values()) {
            sb.append(input.getVoltageLevel().getDescription());
            sb.append(", ");
            sb.append(input.isEnabled());
            sb.append(", ");
            sb.append(input.getDropMenuElement().getTextInputControl1().getText());
            sb.append(", ");
            sb.append(input.getDropMenuElement().getTextInputControl2().getText());
            sb.append("\n");
        }

        return sb.toString();
    }


    private TitleElement getTitlePane(String title) {
        int stackPanePadding = 10;
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.BOTTOM_LEFT);
//        stackPane.setBorder(
//            new Border(
//                new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Paint.valueOf("#007acc"), Color.TRANSPARENT,
//                    BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
//                    CornerRadii.EMPTY, new BorderWidths(2), new Insets(0))
//            )
//        );
        stackPane.setPadding(new Insets(stackPanePadding));
//        stackPane.setBorder(new Border(new BorderStroke(Color.YELLOW, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM)));



        Label text = new Label(title);
//        text.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM)));
        text.getStyleClass().add("label1");
        stackPane.getChildren().add(text);

        Line bottomLine = new Line();
        bottomLine.setStartX(0);
        bottomLine.setStartY(0);
        bottomLine.endXProperty().bind(text.widthProperty()); // todo непонятно, что с этим делать
        bottomLine.setEndY(0);
        bottomLine.setStrokeWidth(2);
        bottomLine.setStroke(Paint.valueOf("#007acc"));

        stackPane.getChildren().add(bottomLine);

        return new TitleElement(stackPane, text, bottomLine, title, stackPanePadding);
    }

    private void prepareVisualComponents(Stage stage) {
        java.awt.Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        Group sceneRoot = new Group();
        sceneRoot.prefHeight(maximumWindowBounds.getHeight());
        sceneRoot.prefWidth(maximumWindowBounds.getWidth());

        prepareGridPane(maximumWindowBounds);

        prepareScrollPane(maximumWindowBounds);

        Scene scene = new Scene(scrollPane, Color.WHITE);
        scene.setFill(Color.WHITE);

        scene.getStylesheets().add("https://fonts.googleapis.com/css?family=Wix+Madefor+Display"); // todo это хороший шрифт Wix Madefor Display
        scene.getStylesheets().add("https://fonts.googleapis.com/css?family=Raleway"); // todo это стандартный Raleway
        scene.getStylesheets().add(getClass().getResource("/css/switch/switch.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/checkbox/checkbox.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/background/background.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/label/label.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/text.input/text_input.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("/fonts/Raleway-Italic-VariableFont_wght.ttf").toExternalForm());
        stage.setTitle("Wavefunction Collapse Algorithm");
        stage.getIcons().add(new Image("C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\com\\example\\demo\\icon.png"));
        stage.setScene(scene);
//        stage.setMaximized(true);

    }

    private void prepareGridPane(java.awt.Rectangle maximumWindowBounds) {
        gridPane = new GridPane();
        gridPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        gridPane.setGridLinesVisible(false);
        gridPane.setPadding(new Insets(20));
        gridPane.setVgap(20);
        gridPane.setHgap(30);
        gridPane.setMaxHeight(maximumWindowBounds.getHeight() - 24);
        gridPane.setMaxWidth(maximumWindowBounds.getWidth() - 1);


        for (int i = 0; i < totalColumns; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100d / totalColumns);
            gridPane.getColumnConstraints().add(column);
        }
    }

    private void prepareScrollPane(java.awt.Rectangle maximumWindowBounds) {
        scrollPane = new ScrollPane();
        scrollPane.setContent(gridPane);

//        scrollPane.prefWidthProperty().bind(gridPane.prefWidthProperty());
//        scrollPane.prefHeightProperty().bind(gridPane.prefHeightProperty());

        scrollPane.setPrefViewportHeight(maximumWindowBounds.getHeight() - 50);
        scrollPane.setPrefViewportWidth(maximumWindowBounds.getWidth() - 15);
        scrollPane.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
    }

    private VoltageLevelInput preparePaneForVoltageLevel(VoltageLevel voltageLevel, int row, int column, int spreadColumns) throws InterruptedException {

        if (spreadColumns < 2) {
            throw new IllegalArgumentException("Parameter totalColumns can`t be less than 2");
        }

        final TitleElement title = getTitlePane(voltageLevel.getDescription());
        GridPane.setValignment(title.getStackPane(), VPos.BOTTOM);
        GridPane.setHalignment(title.getStackPane(), HPos.LEFT);
        gridPane.add(title.getStackPane(), column, row, 2, 1);


        boolean isDropMenuShown = false;

        final DropMenuElement dropMenuElement = getDropMenuPane(voltageLevel, row, column, spreadColumns, isDropMenuShown);

        final CheckBox enabledCheckBox = getCheckbox(voltageLevel);
        GridPane.setValignment(enabledCheckBox, VPos.BOTTOM);
        GridPane.setHalignment(enabledCheckBox, HPos.LEFT);
        gridPane.add(enabledCheckBox, column + 2, row, 2, 1);

        final VoltageLevelInput input = new VoltageLevelInput(title, enabledCheckBox, dropMenuElement, voltageLevel, gridPane, row, column, spreadColumns, isDropMenuShown);

        return input;
    }

    private DropMenuElement getDropMenuPane(VoltageLevel voltageLevel, int row, int column, int spreadColumns, boolean isDropMenuShown) {

        GridPane menuTable = new GridPane();
        menuTable.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), new Insets(0))));

        menuTable.setAlignment(Pos.CENTER_LEFT);
        menuTable.getStyleClass().add("shadow-pane");
        menuTable.setGridLinesVisible(false);
        menuTable.setPadding(new Insets(20));
        menuTable.setVgap(20);
        menuTable.setHgap(5);

        for (int i = 0; i < totalMenuTableColumns; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100d / totalMenuTableColumns);
            menuTable.getColumnConstraints().add(columnConstraints);
        }

        Text field1Title = new Text("Field 1");
        menuTable.add(field1Title, 0, 0, totalMenuTableColumns/2, 1);

        ExtendedControl textInputControl1 = new ExtendedControl();
        textInputControl1.setPromptText("");
        menuTable.add(textInputControl1, totalMenuTableColumns/2, 0, totalMenuTableColumns/2, 1);

        ExtendedControl textInputControl2 = new ExtendedControl();

        textInputControl2.setPromptText("Field 2");
        menuTable.add(textInputControl2, 0, 1, totalMenuTableColumns, 1);

        menuTable.setVisible(isDropMenuShown);
        menuTable.managedProperty().bind(menuTable.visibleProperty());

        gridPane.add(menuTable, column, row + 1, spreadColumns, 1);

        return new DropMenuElement(menuTable, textInputControl1, textInputControl2);
    }

    private CheckBox getCheckbox(VoltageLevel voltageLevel) {
        CheckBox checkBox = new Switch("enabled");
        checkBox.setSelected(true);

        return checkBox;
    }

}
