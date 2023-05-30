package com.example.demo.base.service;

import com.example.demo.base.model.configuration.TransformerConfiguration;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NodeCalculateService {

    private final BaseConfiguration baseConfiguration;

    public void setNumberOfNodes(List<TransformerConfiguration> configurations) {
        // todo проверка на отрицательные значения количества узлов и ветвей
        if ((baseConfiguration.getRequiredNumberOfNodes() - 1) > baseConfiguration.getRequiredNumberOfEdges()) {
            throw new UnsupportedOperationException(
                String.format("Количество ветвей=%d не может быть меньше, чем количество узлов=%d минус 1", baseConfiguration.getRequiredNumberOfEdges(), baseConfiguration.getRequiredNumberOfNodes()));
        }



    }


}
