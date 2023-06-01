package com.example.demo.base.model.power;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NodeLineDto {
    private String nodeUuid;
    private String lineUuid;
}
