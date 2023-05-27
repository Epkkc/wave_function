package com.example.demo.base.model.configuration;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class NodeTypeResult {

    private PowerNodeType powerNodeType;
    private VoltageLevel voltageLevel;
    private int numberOfElements;

}
