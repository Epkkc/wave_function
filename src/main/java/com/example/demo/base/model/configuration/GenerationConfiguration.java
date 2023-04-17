package com.example.demo.base.model.configuration;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class GenerationConfiguration {

    private VoltageLevel level;
    private int minPower; // kWt
    private int maxPower; // kWt
    private int boundingArea;
    private boolean enabled;

}
