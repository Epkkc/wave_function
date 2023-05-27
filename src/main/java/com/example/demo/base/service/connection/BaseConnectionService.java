package com.example.demo.base.service.connection;

import com.example.demo.base.model.enums.PowerNodeType;
import com.example.demo.base.model.enums.VoltageLevel;
import com.example.demo.base.model.grid.Matrix;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.power.BaseLine;
import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.base.service.connection.AbstractConnectionService;
import com.example.demo.base.service.element.BaseElementService;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class BaseConnectionService extends AbstractConnectionService<BasePowerNode> {

    public BaseConnectionService(BaseElementService elementService) {
        super(elementService);
    }
}
