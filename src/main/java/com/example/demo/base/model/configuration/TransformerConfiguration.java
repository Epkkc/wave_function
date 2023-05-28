package com.example.demo.base.model.configuration;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class TransformerConfiguration {

    private VoltageLevel level;
    private int boundingAreaFrom; //TODO Предполагается, что это поле будет настраиваемым
    private int boundingAreaTo; //TODO Предполагается, что это поле будет настраиваемым
    private boolean enabled; //TODO Предполагается, что можно будет чекбоксом включить отключить уровни напряжения
    private List<Integer> transformerPowerSet;
    private int numberOfNodes;
    private double maxLineLength; // Максимальная длина линии, которой будет соединён трансформатор с трансформатором

}
