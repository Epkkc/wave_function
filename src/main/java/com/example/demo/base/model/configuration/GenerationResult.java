package com.example.demo.base.model.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class GenerationResult {
    private int numberOfNodes;
    private int numberOfEdges;
    private String fileName;
    private List<NodeTypeResult> resultList;
}
