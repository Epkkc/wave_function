package com.example.demo.base.service.status;

import com.example.demo.base.model.configuration.GeneratorConfiguration;
import com.example.demo.base.model.configuration.LoadConfiguration;
import com.example.demo.base.model.configuration.TransformerConfiguration;
import com.example.demo.base.model.power.AbstractPowerNode;
import com.example.demo.base.model.power.BaseConnection;
import com.example.demo.base.model.status.BaseStatus;

import java.util.List;

public interface StatusService<PNODE extends AbstractPowerNode<? extends BaseStatus, ? extends BaseConnection>> {

    void setTransformerStatusToArea(PNODE powerNode, List<TransformerConfiguration> transformerConfigurations);
    void setLoadStatusToArea(PNODE powerNode, LoadConfiguration loadCfg);
    void setGeneratorStatusToArea(PNODE powerNode, GeneratorConfiguration genCfg);
    void removeStatusesByNodeUuid(String uuid);

}
