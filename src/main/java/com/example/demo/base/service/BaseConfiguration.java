package com.example.demo.base.service;

import com.example.demo.base.model.configuration.GenerationConfiguration;
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
    protected final int delay;
    protected final int numberOfNodes;
    protected final int numberOfEdges;
    protected List<TransformerConfiguration> transformerConfigurations;
    protected List<LoadConfiguration> loadConfigurations;
    protected List<GenerationConfiguration> generationConfigurations;
}
