package com.example.demo.base.service;

import com.example.demo.base.model.configuration.GeneratorConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class BaseConfiguration {
    protected final int rows;
    protected final int columns;
    protected final int requiredNumberOfNodes;
    protected final int requiredNumberOfEdges;
    protected List<TransformerConfiguration> transformerConfigurations;
    protected List<LoadConfiguration> loadConfigurations;
    protected List<GeneratorConfiguration> generatorConfigurations;
}
