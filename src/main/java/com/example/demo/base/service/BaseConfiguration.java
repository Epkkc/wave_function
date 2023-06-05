package com.example.demo.base.service;

import com.example.demo.base.model.configuration.GeneratorConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.status.BaseBlockingStatusConfiguration;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class BaseConfiguration {
    protected final int rows;
    protected final int columns;
    protected final int requiredNumberOfNodes;
    protected final int requiredNumberOfEdges;
    protected Map<VoltageLevel, TransformerConfiguration> transformerConfigurations;
    protected Map<VoltageLevel, LoadConfiguration> loadConfigurations;
    protected Map<VoltageLevel, GeneratorConfiguration> generatorConfigurations;
    protected BaseBlockingStatusConfiguration baseBlockingStatusConfiguration;

    public List<TransformerConfiguration> getTransformerConfigurationList() {
        return transformerConfigurations.values().stream()
            .sorted(Comparator.<TransformerConfiguration>comparingInt(cfg-> cfg.getLevel().getVoltageLevel()).reversed())
            .toList();
    }

    public List<LoadConfiguration> getLoadConfigurationList() {
        return loadConfigurations.values().stream()
            .sorted(Comparator.<LoadConfiguration>comparingInt(cfg-> cfg.getLevel().getVoltageLevel()).reversed())
            .toList();
    }

    public List<GeneratorConfiguration> getGeneratorConfigurationList() {
        return generatorConfigurations.values().stream()
            .sorted(Comparator.<GeneratorConfiguration>comparingInt(cfg-> cfg.getLevel().getVoltageLevel()).reversed())
            .toList();
    }

    public TransformerConfiguration getTransformerConfiguration(VoltageLevel voltageLevel) {
        return transformerConfigurations.get(voltageLevel);
    }

    public LoadConfiguration getLoadConfiguration(VoltageLevel voltageLevel) {
        return loadConfigurations.get(voltageLevel);
    }

    public GeneratorConfiguration getGeneratorConfiguration(VoltageLevel voltageLevel) {
        return generatorConfigurations.get(voltageLevel);
    }

}
