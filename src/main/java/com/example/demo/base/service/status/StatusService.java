package com.example.demo.base.service.status;

import com.example.demo.base.model.configuration.GenerationConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;

public interface StatusService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>> {

    void setTransformerStatusToArea(PNODE powerNode, TransformerConfiguration... levels);

    void setLoadStatusToArea(PNODE powerNode, LoadConfiguration loadCfg);
    void setLoadStatusToArea(PNODE powerNode, GenerationConfiguration genCfg);

}
