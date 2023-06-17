package com.example.demo.base.service;

import com.example.demo.base.model.configuration.GeneratorConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.enums.VoltageLevel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_10;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_110;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_220;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_35;
import static com.example.demo.base.model.enums.VoltageLevel.LEVEL_500;

public class ConfigurationStaticSupplier {

    public static int rows = 40;
    public static int columns = 40;
    public static int numberOfNodes = 20;
    public static int numberOfEdges = 19;
    public static boolean baseExport = false;
    public static boolean cimExport = false;
    public static int baseAlgorithmIterations = 200;
    public static double fxGridPadding = 2d;
    public static double fxGridVGap = 4d;
    public static double fxGridHGap = 4d;
    public static double fxBaseSize = 35;
    public static int fxLineDisappearanceDuration = 3_000;
    public static int cimExportProportionalityFactor = 4;
    public static int cimExportInitialXOffset = 0;
    public static int cimExportInitialYOffset = 0;
    // todo добавить настройки для СИМ-а, такие как сопротивление линий (может быть добавить их в LoadConfiguration)
    public static int fxAlgorithmAfterAllTransformersSetTimeout = 2_000;
    public static boolean deserializationAlgorithmSetStatuses = true;
    public static boolean baseBlockingStatusRoundedArea = false;
    public static int baseBlockingStatusBoundingAreaRadius = 2;
    public static boolean randomFirstNode = false;


    public static final Map<VoltageLevel, TransformerConfiguration> transformerConfigurations = new HashMap<>();
    public static final Map<VoltageLevel, LoadConfiguration> loadConfigurations = new HashMap<>();
    public static final Map<VoltageLevel, GeneratorConfiguration> generatorConfigurations = new HashMap<>();


    static {
        // Трансформаторы напряжением 35/10 кВ имеют следующий ряд мощностей 1000, 1600, 2500, 4000, 6300
        // http://kabelmag2012.narod.ru/TransfS.html
        transformerConfigurations.put(LEVEL_500, TransformerConfiguration.builder()
            .level(LEVEL_500)
            .enabled(false)
                .threeWindingEnabled(true)
            .transformerPowerSet(List.of(10000))
            .boundingAreaFrom(60)
            .boundingAreaTo(64)
            .roundedBoundingArea(true)
            .maxLineLength(64)
            .numberOfNodes(2)
            .maxChainLength(3)
                .gap(2)
                .timeout(2000)
            .build());
        transformerConfigurations.put(LEVEL_220, TransformerConfiguration.builder()
            .level(LEVEL_220)
            .enabled(false)
            .threeWindingEnabled(true)
            .boundingAreaFrom(35)
            .boundingAreaTo(38)
            .roundedBoundingArea(true)
            .maxLineLength(38)
            .transformerPowerSet(List.of(5000))
            .numberOfNodes(3)
            .maxChainLength(3)
            .gap(2)
            .timeout(2000)
            .build());
        transformerConfigurations.put(LEVEL_110, TransformerConfiguration.builder()
            .level(LEVEL_110)
            .enabled(true)
            .threeWindingEnabled(true)
            .boundingAreaFrom(18)
            .boundingAreaTo(20)
            .roundedBoundingArea(true)
            .maxLineLength(20)
            .transformerPowerSet(List.of(2500))
            .numberOfNodes(2)
            .maxChainLength(3)
            .gap(2)
            .timeout(2000)
            .build());
        transformerConfigurations.put(LEVEL_35, TransformerConfiguration.builder()
            .level(LEVEL_35)
            .enabled(true)
            .threeWindingEnabled(false)
            .boundingAreaFrom(6)
            .boundingAreaTo(7)
            .roundedBoundingArea(true)
            .maxLineLength(7)
            .transformerPowerSet(List.of(1000))
            .numberOfNodes(4)
            .maxChainLength(3)
            .gap(1)
            .timeout(1000)
            .build());
        transformerConfigurations.put(LEVEL_10, TransformerConfiguration.builder()
            .level(LEVEL_10)
            .enabled(true)
            .threeWindingEnabled(false)
            .boundingAreaFrom(2)
            .boundingAreaTo(3)
            .roundedBoundingArea(true)
            .maxLineLength(3)
            .transformerPowerSet(List.of(500))
            .numberOfNodes(1000)
            .maxChainLength(3)
            .gap(0)
            .timeout(500)
            .build());

        loadConfigurations.put(LEVEL_10, LoadConfiguration.builder()
            .level(LEVEL_10)
            .enabled(true)
            .minLoad(50)
            .maxLoad(100)
            .boundingAreaFrom(2)
            .boundingAreaTo(4)
            .roundedBoundingArea(true)
            .maxLineLength(4)
            .maxChainLength(5)
            .generationRate(100)
            .maxConnectedFeeders(3)
            .build());
        loadConfigurations.put(LEVEL_35, LoadConfiguration.builder()
            .level(LEVEL_35)
            .enabled(true)
            .minLoad(300)
            .maxLoad(400)
            .boundingAreaFrom(2)
            .boundingAreaTo(4)
            .roundedBoundingArea(true)
            .maxLineLength(4)
            .maxChainLength(1)
            .generationRate(20)
            .maxConnectedFeeders(2)
            .build());

        generatorConfigurations.put(LEVEL_35, GeneratorConfiguration.builder()
            .level(LEVEL_35)
            .enabled(true)
            .minNumberOfBlocks(2)
            .maxNumberOfBlocks(4)
            .blockPower(1500)
            .boundingAreaFrom(2)
            .boundingAreaTo(4)
            .roundedBoundingArea(true)
            .generationRate(60)
            .build());
        generatorConfigurations.put(LEVEL_110, GeneratorConfiguration.builder()
            .level(LEVEL_110)
            .enabled(true)
            .minNumberOfBlocks(3)
            .maxNumberOfBlocks(6)
            .blockPower(2400)
            .boundingAreaFrom(2)
            .boundingAreaTo(5)
            .roundedBoundingArea(true)
            .generationRate(70)
            .build());
        generatorConfigurations.put(LEVEL_220, GeneratorConfiguration.builder()
            .level(LEVEL_220)
            .enabled(false)
            .minNumberOfBlocks(3)
            .maxNumberOfBlocks(6)
            .boundingAreaFrom(2)
            .boundingAreaTo(5)
            .build());
        generatorConfigurations.put(LEVEL_500, GeneratorConfiguration.builder()
            .level(LEVEL_500)
            .enabled(false)
            .minNumberOfBlocks(3)
            .maxNumberOfBlocks(6)
            .blockPower(600)
            .boundingAreaFrom(2)
            .boundingAreaTo(5)
            .roundedBoundingArea(true)
            .build());
    }
}
