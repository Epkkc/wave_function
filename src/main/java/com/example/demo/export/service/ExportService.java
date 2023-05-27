package com.example.demo.export.service;

import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.power.BaseConnection;

public interface ExportService<T extends AbstractBasePowerNode<? extends BaseConnection>> {
    String saveAsFile();
}
