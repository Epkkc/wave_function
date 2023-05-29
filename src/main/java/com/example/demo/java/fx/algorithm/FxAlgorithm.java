package com.example.demo.java.fx.algorithm;

import com.example.demo.base.algorithm.AbstractAlgorithm;
import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.configuration.GeneratorConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.service.connection.ConnectionService;
import com.example.demo.base.service.element.ElementService;
import com.example.demo.base.service.status.StatusService;
import com.example.demo.export.service.ExportService;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.java.fx.model.power.FxPowerLine;
import com.example.demo.java.fx.service.FxConfiguration;
import com.example.demo.thread.StoppableThread;

import java.util.List;

public class FxAlgorithm extends AbstractAlgorithm<FxAbstractPowerNode, FxPowerLine, FxConfiguration> {

    public FxAlgorithm(Matrix<FxAbstractPowerNode> matrix, ElementService<FxAbstractPowerNode, FxPowerLine> elementService, StatusService<FxAbstractPowerNode> statusService,
                       ConnectionService<FxAbstractPowerNode> connectionService, FxConfiguration configuration, List<TransformerConfiguration> transformerConfigurations,
                       List<LoadConfiguration> loadConfigurations, List<GeneratorConfiguration> generatorConfigurations, PowerNodeFactory<FxAbstractPowerNode> nodeFactory,
                       ExportService<FxAbstractPowerNode, FxPowerLine> exportService, boolean randomFirst) {
        super(matrix, elementService, statusService, connectionService, configuration, transformerConfigurations, loadConfigurations, generatorConfigurations, nodeFactory, exportService, randomFirst);
    }

    @Override
    protected void afterTransformerSet(TransformerConfiguration configuration) {
        sleep(configuration.getLevel().getTimeout());
    }

    @Override
    protected void afterAllTransformersSet() {
        sleep(4000);
    }

    @Override
    protected void afterLoadSet(LoadConfiguration configuration) {
        sleep(configuration.getLevel().getTimeout());
    }

    @Override
    protected void afterAllLoadSet() {
        sleep(4000);
    }

    @Override
    protected void afterGeneratorSet(GeneratorConfiguration configuration) {
        sleep(configuration.getLevel().getTimeout());
    }

    private void sleep(int delay) {
        do {
            // Задержка для удобства просмотра
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Обработка остановки потока
        } while (((StoppableThread) Thread.currentThread()).isStopped());
    }
}
