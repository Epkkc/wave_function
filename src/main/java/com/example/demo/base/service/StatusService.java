package com.example.demo.base.service;

import com.example.demo.base.model.configuration.GenerationConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.power.BasePowerNode;

public interface StatusService {

    void setTransformerStatusToArea(BasePowerNode powerNode, TransformerConfiguration... levels);

    void setLoadStatusToArea(BasePowerNode powerNode, LoadConfiguration loadCfg);
    void setLoadStatusToArea(BasePowerNode powerNode, GenerationConfiguration genCfg);

}
