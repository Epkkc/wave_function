package com.example.demo.deserealisation;

import com.example.demo.base.model.configuration.GeneratorConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.LevelChainNumberDto;
import com.example.demo.base.service.BaseTopologyService;
import com.example.demo.base.service.ConfigurationStaticSupplier;
import com.example.demo.base.service.TopologyService;
import com.example.demo.deserealisation.service.DeserializationService;
import com.example.demo.export.dto.PowerLineDto;
import com.example.demo.export.dto.PowerNodeDto;
import com.example.demo.export.dto.SaveDto;
import com.example.demo.java.fx.factories.FxPowerNodeAbstractFactory;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.java.fx.model.power.FxBaseNode;
import com.example.demo.java.fx.model.power.FxPowerLine;
import com.example.demo.java.fx.service.FxConfiguration;
import com.example.demo.java.fx.service.FxConnectionService;
import com.example.demo.java.fx.service.FxElementService;
import com.example.demo.java.fx.service.FxStatusService;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeserializeMain extends Application {

    static SaveDto saveDto;
    static FxConfiguration configuration;
    static Matrix<FxAbstractPowerNode> matrix;
    static GridPane gridPane;
    static FxElementService elementService;
    static FxStatusService statusService;
    static DeserializationService deserializationService = new DeserializationService();

    static String path = "C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\schemes\\";
    static String fileName = "scheme_05_06_2023T11_14_31_662.json";
    static boolean SET_STATUSES = ConfigurationStaticSupplier.deserializationAlgorithmSetStatuses;

    public static void main(String[] args) {
        saveDto = deserializationService.extractSaveDto(path + fileName);
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        int rows = saveDto.getRows();
        int columns = saveDto.getColumns();

        // -1 - заглушечные данные
        configuration = new FxConfiguration(rows, columns, -1, -1, ConfigurationStaticSupplier.fxGridPadding, ConfigurationStaticSupplier.fxGridVGap, ConfigurationStaticSupplier.fxGridHGap, ConfigurationStaticSupplier.fxBaseSize);
        configuration.setTransformerConfigurations(saveDto.getTransformerConfigurations());
        configuration.setLoadConfigurations(saveDto.getLoadConfigurations());
        configuration.setGeneratorConfigurations(saveDto.getGeneratorConfigurations());
        configuration.setBaseBlockingStatusConfiguration(saveDto.getBaseBlockingStatusConfiguration());

        matrix = new Matrix<>(rows, columns);

        fillGraphElements(stage, configuration);
        fillMatrix(rows, columns, elementService);

        TopologyService<FxAbstractPowerNode, FxPowerLine> topologyService = new BaseTopologyService<>(elementService);

        FxConnectionService connectionService = new FxConnectionService(elementService, configuration, topologyService);

        FxPowerNodeAbstractFactory fabric = new FxPowerNodeAbstractFactory(elementService);

        statusService = new FxStatusService(matrix, configuration);

        stage.show();

        Thread thread = new Thread(() -> {
            // Расстановка node-ов по карте
            for (PowerNodeDto nodeDto : saveDto.getMatrix()) {
                if (PowerNodeType.EMPTY.equals(nodeDto.getNodeType())) {
                    continue;
                }

                FxAbstractPowerNode node = fabric.createNode(nodeDto.getNodeType(), nodeDto.getX(), nodeDto.getY(), nodeDto.getPower(),
                    nodeDto.getVoltageLevels().stream().map(level -> new LevelChainNumberDto(level, 0)).collect(
                        Collectors.toList()));
                node.setUuid(nodeDto.getUuid());

                elementService.addPowerNodeToGrid(node);

                if (SET_STATUSES) {
                    addStatuses(node);
                }

                node.getStackPane().requestLayout();
            }

            // Нанесение линий электропередачи на схему
            for (PowerLineDto line : saveDto.getLines()) {
                Optional<FxAbstractPowerNode> point1 = matrix.getNode(line.getPoint1().getX(), line.getPoint1().getY());
                Optional<FxAbstractPowerNode> point2 = matrix.getNode(line.getPoint2().getX(), line.getPoint2().getY());
                connectionService.connectNodes(point1.get(), point2.get(), line.getVoltageLevel(), line.getBreaker() != null && line.getBreaker());
            }
        });
        thread.start();


    }

    private void addStatuses(FxAbstractPowerNode node) {
        switch (node.getNodeType()) {
            case SUBSTATION -> statusService.setTransformerStatusToArea(node, getTransformerConfigurations(node));
            case LOAD -> statusService.setLoadStatusToArea(node, getLoadConfiguration(node));
            case GENERATOR -> statusService.setGeneratorStatusToArea(node, getGeneratorConfiguration(node));
        }
    }

    private List<TransformerConfiguration> getTransformerConfigurations(FxAbstractPowerNode node) {
        return node.getVoltageLevels().stream().map(level -> configuration.getTransformerConfiguration(level)).toList();
    }

    private LoadConfiguration getLoadConfiguration(FxAbstractPowerNode node) {
        return configuration.getLoadConfiguration(node.getVoltageLevels().get(0));
    }

    private GeneratorConfiguration getGeneratorConfiguration(FxAbstractPowerNode node) {
        return configuration.getGeneratorConfiguration(node.getVoltageLevels().get(0));
    }

    private static void fillMatrix(int rows, int columns, FxElementService elementService) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        FxAbstractPowerNode powerNode = new FxBaseNode(i, j, elementService.getBaseSize());
                        matrix.fill(powerNode);
                        GridPane.setConstraints(powerNode.getStackPane(), j, i);
                        gridPane.getChildren().add(powerNode.getStackPane());
                    }
                }
                return null;
            }
        };
        task.run();

    }

    public static void fillGraphElements(Stage stage, FxConfiguration cfg) {
        gridPane = new GridPane();
        gridPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        gridPane.setGridLinesVisible(false);
        gridPane.setPadding(new Insets(cfg.getPadding()));
        gridPane.setVgap(cfg.getVGap());
        gridPane.setHgap(cfg.getHGap());

        Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        Group sceneRoot = new Group();

        javafx.scene.control.ScrollPane scrollPane = new ScrollPane();
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

        elementService = new FxElementService(matrix, cfg, scrollPane, root, gridPane);
    }

}
