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

    public static int rows = 16;
    public static int columns = 30;
    public static int numberOfNodes = 20;
    public static int numberOfEdges = 19;
    public static boolean baseExport = true;
    public static boolean cimExport = false;
    public static int baseAlgorithmIterations = 1;
    public static double fxGridPadding = 2d;
    public static double fxGridVGap = 4d;
    public static double fxGridHGap = 4d;
    public static double fxBaseSize = 45;
    public static int fxLineDisappearanceDuration = 3_000;
    public static int cimExportProportionalityFactor = 4;
    public static int cimExportInitialXOffset = 0;
    public static int cimExportInitialYOffset = 0;
    // todo добавить настройки для СИМ-а, такие как сопротивление линий (может быть добавить их в LoadConfiguration)
    public static int fxAlgorithmAfterAllTransformersSetTimeout = 2_000;
    public static boolean deserializationAlgorithmSetStatuses = false;


    public static final Map<VoltageLevel, TransformerConfiguration> transformerConfigurations = new HashMap<>();
    public static final Map<VoltageLevel, LoadConfiguration> loadConfigurations = new HashMap<>();
    public static final Map<VoltageLevel, GeneratorConfiguration> generatorConfigurations = new HashMap<>();


    static {
        // Трансформаторы напряжением 35/10 кВ имеют следующий ряд мощностей 1000, 1600, 2500, 4000, 6300
        // http://kabelmag2012.narod.ru/TransfS.html
        transformerConfigurations.put(LEVEL_500, TransformerConfiguration.builder()
            .level(LEVEL_500)
            .enabled(false)
            .boundingAreaFrom(LEVEL_500.getBoundingArea())
            .boundingAreaTo(LEVEL_500.getBoundingArea() + 4)
            .roundedBoundingArea(true)
            .maxLineLength(LEVEL_500.getBoundingArea() + 4)
            .transformerPowerSet(List.of(10000))
            .numberOfNodes(2)
            .maxChainLength(3)
            .build());
        transformerConfigurations.put(LEVEL_220, TransformerConfiguration.builder()
            .level(LEVEL_220)
            .enabled(false)
            .boundingAreaFrom(LEVEL_220.getBoundingArea())
            .boundingAreaTo(LEVEL_220.getBoundingArea() + 3)
            .roundedBoundingArea(true)
            .maxLineLength(LEVEL_220.getBoundingArea() + 3)
            .transformerPowerSet(List.of(5000))
            .numberOfNodes(3)
            .maxChainLength(3)
            .build());
        transformerConfigurations.put(LEVEL_110, TransformerConfiguration.builder()
            .level(LEVEL_110)
            .enabled(true)
            .boundingAreaFrom(LEVEL_110.getBoundingArea())
            .boundingAreaTo(LEVEL_110.getBoundingArea() + 2)
            .roundedBoundingArea(true)
            .maxLineLength(LEVEL_110.getBoundingArea() + 2)
            .transformerPowerSet(List.of(2500))
            .numberOfNodes(2)
            .maxChainLength(3)
            .build());
        transformerConfigurations.put(LEVEL_35, TransformerConfiguration.builder()
            .level(LEVEL_35)
            .enabled(true)
            .boundingAreaFrom(LEVEL_35.getBoundingArea())
            .boundingAreaTo(LEVEL_35.getBoundingArea() + 1)
            .roundedBoundingArea(true)
            .maxLineLength(LEVEL_35.getBoundingArea() + 1)
            .transformerPowerSet(List.of(1000))
            .numberOfNodes(4)
            .maxChainLength(3)
            .build());
        transformerConfigurations.put(LEVEL_10, TransformerConfiguration.builder()
            .level(LEVEL_10)
            .enabled(true)
            .boundingAreaFrom(LEVEL_10.getBoundingArea())
            .boundingAreaTo(LEVEL_10.getBoundingArea() + 1)
            .roundedBoundingArea(true)
            .maxLineLength(LEVEL_10.getBoundingArea() + 1)
            .transformerPowerSet(List.of(500))
            .numberOfNodes(1000)
            .maxChainLength(3)
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
            .enabled(false)
            .minLoad(300)
            .maxLoad(400)
            .boundingAreaFrom(2)
            .boundingAreaTo(4)
            .roundedBoundingArea(true)
            .maxLineLength(4)
            .maxChainLength(1)
            .generationRate(40)
            .maxConnectedFeeders(2)
            .build());

        generatorConfigurations.put(LEVEL_35, GeneratorConfiguration.builder()
            .level(LEVEL_35)
            .enabled(true)
            .minNumberOfBlocks(2)
            .maxNumberOfBlocks(4)
            .blockPower(1500)
            .boundingArea(LEVEL_35.getBoundingArea() - 4)
            .transformerArea(2)
            .roundedBoundingArea(true)
            .build());
        generatorConfigurations.put(LEVEL_110, GeneratorConfiguration.builder()
            .level(LEVEL_110)
            .enabled(true)
            .minNumberOfBlocks(3)
            .maxNumberOfBlocks(6)
            .blockPower(2400)
            .boundingArea(LEVEL_110.getBoundingArea() - 4)
            .transformerArea(3)
            .roundedBoundingArea(true)
            .build());
        generatorConfigurations.put(LEVEL_220, GeneratorConfiguration.builder()
            .level(LEVEL_220)
            .enabled(false)
            .minNumberOfBlocks(3)
            .maxNumberOfBlocks(6)
            .blockPower(400)
            .boundingArea(LEVEL_220.getBoundingArea() - 2).transformerArea(3)
            .build());
        generatorConfigurations.put(LEVEL_500, GeneratorConfiguration.builder()
            .level(LEVEL_500)
            .enabled(false)
            .minNumberOfBlocks(3)
            .maxNumberOfBlocks(6)
            .blockPower(600)
            .boundingArea(LEVEL_500.getBoundingArea() - 2)
            .transformerArea(3)
            .roundedBoundingArea(true)
            .build());
    }
}
