package com.example.demo.export.service;

import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.AbstractLine;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;
import com.example.demo.base.service.BaseConfiguration;
import com.example.demo.base.service.element.ElementService;
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

@RequiredArgsConstructor
public class AbstractExportService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>, LINE extends AbstractLine<PNODE>> implements ExportService<PNODE, LINE> {

    protected final ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    protected final BaseConfiguration configuration;
    protected final ElementService<PNODE, LINE> elementService;
    protected final Matrix<PNODE> matrix;

    public String saveAsFile() {
        SaveDto dto = SaveDto.builder()
            .rows(configuration.getRows())
            .columns(configuration.getColumns())
            .matrix(matrix.toNodeList().stream().map(this::mapNodeToDto).collect(Collectors.toList()))
            .lines(elementService.getLines().stream().map(this::mapLineToDto).collect(Collectors.toList()))
            .build();

        final String PREFIX = "scheme_";
        String date = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd_MM_yyyy'T'hh_mm_ss_SSS"));
        String fileName = "C:\\Users\\mnikitin\\IdeaProjects\\other\\demo\\src\\main\\resources\\schemes\\" + PREFIX + date;
        File file = new File(fileName);
        System.out.println("File name: " + PREFIX + date);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(objectMapper.writeValueAsString(dto));
        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }
        return fileName;
    }

    // TODO Вынести в MapStruct маппер
    private PowerLineDto mapLineToDto(LINE line) {
        return PowerLineDto.builder()
            .point1(mapNodeToDto(line.getPoint1()))
            .point2(mapNodeToDto(line.getPoint2()))
            .uuid(line.getUuid())
            .voltageLevel(line.getVoltageLevel())
            .breaker(line.isBreaker())
            .build();
    }

    private PowerNodeDto mapNodeToDto(PNODE node) {
        return PowerNodeDto.builder()
            .nodeType(node.getNodeType())
            .x(node.getX())
            .y(node.getY())
            .power(node.getPower())
            .uuid(node.getUuid())
            .voltageLevels(node.getVoltageLevels())
            .build();
    }

}
