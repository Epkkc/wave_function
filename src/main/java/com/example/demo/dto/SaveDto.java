package com.example.demo.dto;

import com.example.demo.model.Matrix;
import com.example.demo.model.PowerLine;
import com.example.demo.model.power.node.PowerNode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class SaveDto {
    int rows;
    int columns;
    private Collection<PowerNodeDto> matrix;
    private List<PowerLineDto> lines;
}
