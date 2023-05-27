package com.example.demo.export.dto;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PowerLineDto {

    private PowerNodeDto point1;
    private PowerNodeDto point2;
    private VoltageLevel voltageLevel;
    private String uuid;
    private Boolean breaker;

}
