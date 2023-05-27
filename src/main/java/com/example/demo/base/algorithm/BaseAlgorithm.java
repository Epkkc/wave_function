package com.example.demo.base.algorithm;

import com.example.demo.base.factories.PowerNodeFactory;
import com.example.demo.base.model.configuration.GenerationConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.element.BaseElementService;
import com.example.demo.base.service.ConnectionService;
import com.example.demo.base.service.status.StatusService;
import com.example.demo.export.service.ExportService;

import java.util.List;

public class BaseAlgorithm extends AbstractBaseAlgorithm<BasePowerNode>{
    public BaseAlgorithm(Matrix<BasePowerNode> matrix, BaseElementService elementService,
                         StatusService<BasePowerNode> statusService, ConnectionService connectionService,
                         BaseConfiguration configuration,
                         List<TransformerConfiguration> transformerConfigurations,
                         List<LoadConfiguration> loadConfigurations,
                         List<GenerationConfiguration> generationConfigurations,
                         PowerNodeFactory<BasePowerNode> nodeFactory, ExportService exportService) {
        super(matrix, elementService, statusService, connectionService, configuration, transformerConfigurations, loadConfigurations, generationConfigurations, nodeFactory, exportService);
    }


}
