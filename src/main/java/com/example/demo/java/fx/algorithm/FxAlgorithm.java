package com.example.demo.java.fx.algorithm;

import com.example.demo.base.algorithm.AbstractAlgorithm;
import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.service.ConfigurationStaticSupplier;
import com.example.demo.base.service.TopologyService;
import com.example.demo.base.model.configuration.GeneratorConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.service.connection.ConnectionService;
import com.example.demo.base.service.element.ElementService;
import com.example.demo.base.service.status.StatusService;
import com.example.demo.export.cim.BaseCimExportService;
import com.example.demo.export.cim.CimExportService;
import com.example.demo.export.service.ExportService;
import com.example.demo.java.fx.model.power.FxAbstractPowerNode;
import com.example.demo.java.fx.model.power.FxBaseNode;
import com.example.demo.java.fx.model.power.FxPowerLine;
import com.example.demo.java.fx.service.FxConfiguration;
import com.example.demo.thread.StoppableThread;

public class FxAlgorithm extends AbstractAlgorithm<FxAbstractPowerNode, FxPowerLine, FxConfiguration> {

    public FxAlgorithm(Matrix<FxAbstractPowerNode> matrix,
                       ElementService<FxAbstractPowerNode, FxPowerLine> elementService,
                       StatusService<FxAbstractPowerNode> statusService,
                       ConnectionService<FxAbstractPowerNode> connectionService,
                       TopologyService<FxAbstractPowerNode, FxPowerLine> topologyService,
                       FxConfiguration configuration,
                       PowerNodeFactory<FxAbstractPowerNode> nodeFactory,
                       ExportService<FxAbstractPowerNode, FxPowerLine> exportService,
                       CimExportService<FxAbstractPowerNode, FxPowerLine> cimExportService,
                       boolean randomFirst) {
        super(matrix,
            elementService,
            statusService,
            connectionService,
            topologyService,
            configuration,
            nodeFactory,
            exportService,
            cimExportService,
            randomFirst);
    }

    @Override
    protected FxAbstractPowerNode getBaseNode(int x, int y) {
        return new FxBaseNode(x, y, configurationService.getBaseSize());
    }

    @Override
    protected void afterTransformerSet(TransformerConfiguration configuration) {
        sleep(configuration.getTimeout());
    }

    @Override
    protected void afterAllTransformersSet() {
        sleep(ConfigurationStaticSupplier.fxAlgorithmAfterAllTransformersSetTimeout);
    }

    @Override
    protected void afterLoadSet(LoadConfiguration configuration) {
//        sleep(configuration.getLevel().getTimeout());
        sleep(500);
    }


    @Override
    protected void afterGeneratorSet(GeneratorConfiguration configuration) {
        sleep(1000);
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
