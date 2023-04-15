package com.example.demo.model.power.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class GenerationConfiguration {

    private VoltageLevel level;
    private int minPower; // kWt
    private int maxPower; // kWt
    private int boundingAreaFrom; //TODO Предполагается, что это поле будет настраиваемым
    private int boundingAreaTo; //TODO Предполагается, что это поле будет настраиваемым
    private boolean enabled; //TODO Предполагается, что можно будет чекбоксом включить отключить уровни напряжения

}
