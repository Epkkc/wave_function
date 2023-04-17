package com.example.demo.export.dto;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
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
