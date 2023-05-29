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
public class LoadConfiguration {

    private VoltageLevel level;
    private int minLoad; // kWt
    private int maxLoad; // kWt
    private int boundingAreaFrom;
    private int boundingAreaTo;
    private boolean enabled;
    private int maxChainLinks; // Максимальное число звеньев цепи, состоящей из одинаковых элементов
    private double generationRate; // Шанс генерации ноды
    private double maxLineLength; // Максимальная длина линии, которой будет соединён генератор с трансформатором и другими нагрузками
    private int maxChainLength; // Максимальная длина цепочки нагрузок

}
