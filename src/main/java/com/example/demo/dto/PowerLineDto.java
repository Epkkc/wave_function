package com.example.demo.dto;

import com.example.demo.model.power.node.VoltageLevel;
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

}
