package com.example.demo.base.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BaseConfiguration {
    protected final int rows;
    protected final int columns;
    protected final int delay;
}
