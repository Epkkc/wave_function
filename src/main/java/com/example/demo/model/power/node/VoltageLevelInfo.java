package com.example.demo.model.power.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class VoltageLevelInfo {

    private VoltageLevel level;
    private int boundingAreaFrom; //TODO Предполагается, что это поле будет настраиваемым
    private int boundingAreaTo; //TODO Предполагается, что это поле будет настраиваемым
    private boolean enabled; //TODO Предполагается, что можно будет чекбоксом включить отключить уровни напряжения
    private List<Integer> transformerPowerSet;

}
