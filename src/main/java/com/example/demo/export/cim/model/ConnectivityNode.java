package com.example.demo.export.cim.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@XmlRootElement(name = "cim:ConnectivityNode")
public class ConnectivityNode extends BaseElement {

//      <cim:ConnectivityNode rdf:ID = "_CN_8">
//        <cim:IdentifiedObject.mRID>CN_8</cim:IdentifiedObject.mRID>
//        <cim:IdentifiedObject.name>Bus8</cim:IdentifiedObject.name>
//        <nti:IdentifiedObject.projectID>rastrwin</nti:IdentifiedObject.projectID>
//        <nti:ConnectivityNode.rastrV>90.93696</nti:ConnectivityNode.rastrV>
//        <nti:ConnectivityNode.initialVoltage>0.0</nti:ConnectivityNode.initialVoltage>
//      </cim:ConnectivityNode>

    public ConnectivityNode(String rdfId, String mRID, String name, Double rastrV, Double initialVoltage) {
        super(rdfId, mRID, name);
        this.rastrV = rastrV;
        this.initialVoltage = initialVoltage;
    }

    // todo Эти поля скорее всего не нужны

    @XmlElement(name = "nti:ConnectivityNode.rastrV", required = true)
    private final Double rastrV;

    @XmlElement(name = "ti:ConnectivityNode.initialVoltage", required = true)
    private final Double initialVoltage;

    @XmlTransient
    private List<Terminal> terminals = new ArrayList<>();

    public void addTerminal(Terminal terminal){
        terminals.add(terminal);
    }

}
