package com.example.demo.deserealisation.service;

import com.example.demo.export.dto.SaveDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;

public class DeserializationService {

    public SaveDto extractSaveDto(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        SaveDto saveDto = null;
        try (FileReader reader = new FileReader(path)) {
            saveDto = objectMapper.readValue(reader, SaveDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Exception during parsing", e);
        }
        System.out.println("Parsing successfully ends");

        return saveDto;
    }


}
