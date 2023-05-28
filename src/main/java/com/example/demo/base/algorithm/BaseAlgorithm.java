package com.example.demo.base.algorithm;

import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.configuration.GeneratorConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BaseLine;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.element.BaseElementService;
import com.example.demo.base.service.connection.ConnectionService;
import com.example.demo.base.service.status.StatusService;
import com.example.demo.export.service.BaseExportService;

import java.util.List;

public class BaseAlgorithm extends AbstractAlgorithm<BasePowerNode, BaseLine, BaseConfiguration> {
    public BaseAlgorithm(Matrix<BasePowerNode> matrix, BaseElementService elementService,
                         StatusService<BasePowerNode> statusService, ConnectionService<BasePowerNode> connectionService,
                         BaseConfiguration configuration,
                         List<TransformerConfiguration> transformerConfigurations,
                         List<LoadConfiguration> loadConfigurations,
                         List<GeneratorConfiguration> generatorConfigurations,
                         PowerNodeFactory<BasePowerNode> nodeFactory, BaseExportService exportService, boolean randomFirst) {
        super(matrix, elementService, statusService, connectionService, configuration, transformerConfigurations, loadConfigurations, generatorConfigurations, nodeFactory, exportService, randomFirst);
    }

}
