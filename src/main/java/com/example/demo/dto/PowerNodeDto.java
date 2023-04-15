package com.example.demo.dto;

import com.example.demo.model.power.node.PowerNodeType;
import com.example.demo.model.power.node.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PowerNodeDto {

    private PowerNodeType nodeType;
    private int x;
    private int y;
    private int power;
    private String uuid;
    private Collection<VoltageLevel> voltageLevels;

}
