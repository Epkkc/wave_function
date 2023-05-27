package com.example.demo.export.service;

import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;

public interface ExportService<PNODE extends AbstractBasePowerNode<? extends BaseStatus, ? extends BaseConnection>> {
    String saveAsFile();
}
