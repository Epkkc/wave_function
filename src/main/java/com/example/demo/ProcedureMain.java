package com.example.demo;

import com.example.demo.dto.SaveDto;
import com.example.demo.model.Matrix;
import com.example.demo.model.power.node.BaseNode;
import com.example.demo.model.power.node.GenerationConfiguration;
import com.example.demo.model.power.node.LoadConfiguration;
import com.example.demo.model.power.node.PowerNode;
import com.example.demo.model.power.node.VoltageLevelInfo;
import com.example.demo.procedure.AbstractNodeFabric;
import com.example.demo.procedure.ProcedureAlgorithm;
import com.example.demo.services.Configuration;
import com.example.demo.services.ConnectionService;
import com.example.demo.services.ConnectionServiceImpl;
import com.example.demo.services.ElementServiceImpl;
import com.example.demo.services.StatusService;
import com.example.demo.thread.StoppableThread;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.demo.model.power.node.VoltageLevel.LEVEL_10;
import static com.example.demo.model.power.node.VoltageLevel.LEVEL_110;
import static com.example.demo.model.power.node.VoltageLevel.LEVEL_220;
import static com.example.demo.model.power.node.VoltageLevel.LEVEL_35;
import static com.example.demo.model.power.node.VoltageLevel.LEVEL_500;

public class ProcedureMain extends Application {

    static int rows = 20;
    static int columns = 20;
    static StoppableThread thread;
    static Configuration cfg;
    static ElementServiceImpl elementService;
    static Matrix<PowerNode> matrix;
    static GridPane gridPane;
//    static ObjectMapper objectMapper = new ObjectMapper();



    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        cfg = new Configuration(rows, columns);

        matrix = new Matrix<>(rows, columns);

        fillGraphElements(stage, cfg);

        fillMatrix();

        StatusService statusService = new StatusService(matrix, true);

        ConnectionService connectionService = new ConnectionServiceImpl(elementService);

        AbstractNodeFabric fabric = new AbstractNodeFabric(elementService);

        List<VoltageLevelInfo> voltageLevels = new ArrayList<>();

        // TODO добавлять сюда в зависимости от положения чекбокса (VoltageLevelInfo.enabled)
        // TODO также заполнять boundingArea теми значениями, которые заполнит пользователь
//        voltageLevels.add(VoltageLevelInfo.builder().level(LEVEL_500).boundingAreaFrom(LEVEL_500.getBoundingArea()).boundingAreaTo(LEVEL_500.getBoundingArea()+4).transformerPowerSet(List.of(10000)).build());
        voltageLevels.add(VoltageLevelInfo.builder().level(LEVEL_220).boundingAreaFrom(LEVEL_220.getBoundingArea()).boundingAreaTo(LEVEL_220.getBoundingArea()+3).transformerPowerSet(List.of(5000)).build());
        voltageLevels.add(VoltageLevelInfo.builder().level(LEVEL_110).boundingAreaFrom(LEVEL_110.getBoundingArea()).boundingAreaTo(LEVEL_110.getBoundingArea()+2).transformerPowerSet(List.of(2500)).build());
        voltageLevels.add(VoltageLevelInfo.builder().level(LEVEL_35).boundingAreaFrom(LEVEL_35.getBoundingArea()).boundingAreaTo(LEVEL_35.getBoundingArea()+1).transformerPowerSet(List.of(1000)).build());
        voltageLevels.add(VoltageLevelInfo.builder().level(LEVEL_10).boundingAreaFrom(LEVEL_10.getBoundingArea()).boundingAreaTo(LEVEL_10.getBoundingArea()+1).transformerPowerSet(List.of(500)).build());

        List<LoadConfiguration> loadConfigurations = new ArrayList<>();
        // Трансформаторы напряжением 35/10 кВ имеют следующий ряд мощностей 1000, 1600, 2500, 4000, 6300
        // http://kabelmag2012.narod.ru/TransfS.html
        loadConfigurations.add(LoadConfiguration.builder().level(LEVEL_10).minLoad(40).maxLoad(200).boundingAreaFrom(2).build());
        loadConfigurations.add(LoadConfiguration.builder().level(LEVEL_35).minLoad(300).maxLoad(500).boundingAreaFrom(3).build());

//        List<GenerationConfiguration> generationConfigurations = new ArrayList<>();
//        generationConfigurations.add(GenerationConfiguration.builder().level(LEVEL_110).minPower(50).maxPower(70).boundingAreaFrom().build());
//        generationConfigurations.add(GenerationConfiguration.builder().level(LEVEL_220).minPower(150).maxPower(200).boundingAreaFrom().build());
//        generationConfigurations.add(GenerationConfiguration.builder().level(LEVEL_500).minPower(500).maxPower(600).boundingAreaFrom(40).build());



        ProcedureAlgorithm procedureAlgorithm = new ProcedureAlgorithm(matrix, elementService, statusService, connectionService, cfg, voltageLevels, loadConfigurations, fabric);


        thread = new StoppableThread(procedureAlgorithm::start);
        thread.setName("Didli");
        thread.setDaemon(true);

        stage.show();

        thread.start();


        // Для демонстрации
//        PowerNode generator = new Generator(elementService.getBaseSize(), LEVEL_220);
//        GridPane.setConstraints(generator.getStackPane(), 0, rows + 1);
//        gridPane.getChildren().add(generator.getStackPane());
//
//        PowerNode load = new Load(elementService.getBaseSize(), LEVEL_110);
//        GridPane.setConstraints(load.getStackPane(), 1, rows + 1);
//        gridPane.getChildren().add(load.getStackPane());

//        PowerNode load = new ThreeWSubStation(elementService.getBaseSize(), LEVEL_500, LEVEL_220, LEVEL_110);
//        GridPane.setConstraints(load.getStackPane(), 1, rows + 1);
//        gridPane.getChildren().add(load.getStackPane());

//        SaveDto dto = SaveDto.builder()
//            .matrix(elementService.getMatrix())
//            .lines(elementService.getLines())
//            .build();
//
//        final String PREFIX = "scheme_";
//        String format = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"));\
////        FileWriter writer = new FileWriter(PREFIX + format);
//
//        try (FileWriter writer = new FileWriter(PREFIX + format)) {
//            writer.write(objectMapper.writeValueAsString(dto));
//        } catch (Exception e) {
//            //smth
//        }

    }

    private static void fillMatrix() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                PowerNode powerNode = new BaseNode(elementService.getBaseSize());
                powerNode.setX(i);
                powerNode.setY(j);
                matrix.fill(powerNode);
                GridPane.setConstraints(powerNode.getStackPane(), j, i);
                gridPane.getChildren().add(powerNode.getStackPane());
            }
        }
    }

    public static void fillGraphElements(Stage stage, Configuration cfg) {
        gridPane = new GridPane();
        gridPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        gridPane.setGridLinesVisible(false);
        gridPane.setPadding(new Insets(cfg.getPadding()));
        gridPane.setVgap(cfg.getVGap());
        gridPane.setHgap(cfg.getHGap());

        Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        Group sceneRoot = new Group();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefViewportHeight(maximumWindowBounds.getHeight() - 38);
        scrollPane.setPrefViewportWidth(maximumWindowBounds.getWidth() - 15);

        sceneRoot.getChildren().add(scrollPane);

        Scene scene = new Scene(sceneRoot, Color.WHITE); // 969faf
        scene.setFill(Color.WHITE);

        Group root = new Group();

        root.getChildren().add(gridPane);

        scrollPane.setContent(root);

        stage.setTitle("Wavefunction Collapse Algorithm");
        stage.getIcons().add(new Image("C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\com\\example\\demo\\icon.png"));
        stage.setScene(scene);
        stage.setMaximized(true);

        StackPane stopMessage = getStopMessageBlock("Для продолжения работы нажмите CAPS LOCK");
        stopMessage.setAlignment(Pos.BOTTOM_CENTER);
        stopMessage.setOpacity(0);
        stopMessage.mouseTransparentProperty().set(true);

        sceneRoot.getChildren().add(stopMessage);
        scene.setOnKeyPressed(keyEvent -> {
            if (KeyCode.CAPS.equals(keyEvent.getCode())) {
                thread.changeStopped();
                System.out.println("Нажатие на CAPS");
                System.out.println("Thread 1 stopped = " + thread.isStopped());
                if (thread.isStopped()) {
                    stopMessage.setOpacity(0.85);
                } else {
                    stopMessage.setOpacity(0);
                }
            }
        });

        elementService = new ElementServiceImpl(cfg, stage, scene, scrollPane, root, gridPane, matrix);
    }

    private static StackPane getStopMessageBlock(String message) {
        int hPadding = 10;
        int vPadding = 7;

        Text text = new Text();
        text.setFont(Font.getDefault());
        text.setText(message);

        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(vPadding, hPadding, vPadding, hPadding));
        stackPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85);");

        stackPane.getChildren().add(text);

        return stackPane;
    }

}
