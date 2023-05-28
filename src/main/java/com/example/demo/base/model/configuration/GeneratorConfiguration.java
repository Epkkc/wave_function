package com.example.demo.base.model.configuration;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString
public class GeneratorConfiguration {

    private VoltageLevel level;
    private int minPower; // kWt
    private int maxPower; // kWt
    private int boundingArea;
    private int transformerArea;
    private boolean enabled;
    private double maxLineLength; // Максимальная длина линии, которой будет соединён генератор с трансформатором

}
