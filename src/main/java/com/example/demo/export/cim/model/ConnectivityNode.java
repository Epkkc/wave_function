package com.example.demo.export.cim.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectivityNode extends BaseElement {

    public ConnectivityNode(String rdfId, String mRID, String name, Double rastrV, Double initialVoltage) {
        super(rdfId, mRID, name);
        this.rastrV = rastrV;
        this.initialVoltage = initialVoltage;
    }

    @XmlElement(name = "ConnectivityNode.rastrV", namespace = "http://nti.mpei.ru/#", required = true)
    private Double rastrV;

    @XmlElement(name = "ConnectivityNode.initialVoltage", namespace = "http://nti.mpei.ru/#", required = true)
    private Double initialVoltage;

    @XmlTransient
    private List<Terminal> terminals = new ArrayList<>();

    public void addTerminal(Terminal terminal){
        terminals.add(terminal);
    }

}
