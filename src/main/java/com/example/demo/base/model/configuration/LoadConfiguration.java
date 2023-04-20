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
    private int boundingArea;
    private int transformerArea;
    private boolean enabled;

}