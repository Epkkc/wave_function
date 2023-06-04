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
    private int minLoad;
    private int maxLoad;
    private int boundingAreaFrom;
    private int boundingAreaTo;
    private boolean enabled;
    private int maxChainLinks; // Максимальное число звеньев цепи, состоящей из одинаковых элементов
    private int generationRate; // Шанс генерации ноды от 0 до 100 %
    private double maxLineLength; // Максимальная длина линии, которой будет соединён генератор с трансформатором и другими нагрузками
    private int maxChainLength; // Максимальная длина цепочки нагрузок
    private int maxConnectedFeeders; // Максимальное число присоединённых фидеров

}
