package com.example.demo.base.model.configuration;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString
public class GeneratorConfiguration {

    private VoltageLevel level;
    private int minNumberOfBlocks;
    private int maxNumberOfBlocks;
    private int blockPower;
    private int boundingArea;
    private int transformerArea;
    private boolean roundedBoundingArea; // Является ли форма boundingArea круглой или квадратной
    private boolean enabled;
    private double maxLineLength; // Максимальная длина линии, которой будет соединён генератор с трансформатором

}
