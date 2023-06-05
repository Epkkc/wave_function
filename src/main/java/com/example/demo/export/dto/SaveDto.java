package com.example.demo.export.dto;

import com.example.demo.base.model.configuration.GeneratorConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.enums.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class SaveDto {
    int rows;
    int columns;
    private Collection<PowerNodeDto> matrix;
    private List<PowerLineDto> lines;
    protected Map<VoltageLevel, TransformerConfiguration> transformerConfigurations;
    protected Map<VoltageLevel, LoadConfiguration> loadConfigurations;
    protected Map<VoltageLevel, GeneratorConfiguration> generatorConfigurations;
}
