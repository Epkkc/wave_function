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
public class GeneralResult {
    private int numberOfNodes;
    private int numberOfEdges;
    private String fileName;
    private String cimFileName;
    private List<NodeTypeResult> resultList;
    private List<String> errorMessage;
}
