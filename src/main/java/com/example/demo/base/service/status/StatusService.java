package com.example.demo.base.service.status;

import com.example.demo.base.model.configuration.GenerationConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.power.AbstractBasePowerNode;
import com.example.demo.base.model.power.BaseConnection;

public interface StatusService<T extends AbstractBasePowerNode<? extends BaseConnection>> {

    void setTransformerStatusToArea(T powerNode, TransformerConfiguration... levels);

    void setLoadStatusToArea(T powerNode, LoadConfiguration loadCfg);
    void setLoadStatusToArea(T powerNode, GenerationConfiguration genCfg);

}
