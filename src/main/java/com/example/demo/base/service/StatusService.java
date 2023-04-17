package com.example.demo.base.service;

import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.VoltageLevelInfo;
import com.example.demo.base.model.power.BasePowerNode;
import com.example.demo.java.fx.model.power.FxPowerNode;

public interface StatusService {

    void setTransformerStatusToArea(BasePowerNode powerNode, VoltageLevelInfo... levels);

    void setLoadStatusToArea(BasePowerNode powerNode, LoadConfiguration loadCfg);

}
