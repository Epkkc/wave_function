package com.example.demo.export.service;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.power.BaseLine;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.element.BaseElementService;
import com.example.demo.export.dto.PowerLineDto;
import com.example.demo.export.dto.PowerNodeDto;
import com.example.demo.export.dto.SaveDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class BaseExportService extends AbstractExportService<BasePowerNode, BaseLine> {

    public BaseExportService(BaseConfiguration configuration, BaseElementService elementService, Matrix<BasePowerNode> matrix) {
        super(configuration, elementService, matrix);
    }
}
