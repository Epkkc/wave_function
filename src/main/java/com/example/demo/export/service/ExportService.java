package com.example.demo.export.service;

import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;

public interface ExportService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>> {
    String saveAsFile();
}
