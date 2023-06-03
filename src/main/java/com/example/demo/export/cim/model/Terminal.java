package com.example.demo.export.cim.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@XmlRootElement(name = "cim:Terminal")
public class Terminal extends BaseElement{

//      <cim:Terminal rdf:ID = "_T_CLoad_8">
//        <cim:Terminal.ConductingEquipment rdf:resource="#_CLoad_8"/>
//        <cim:Terminal.ConnectivityNode rdf:resource="#_CN_8"/>
//        <cim:ACDCTerminal.connected>true</cim:ACDCTerminal.connected>
//        <cim:IdentifiedObject.mRID>T_CLoad_8</cim:IdentifiedObject.mRID>
//        <nti:IdentifiedObject.projectID>rastrwin</nti:IdentifiedObject.projectID>
//      </cim:Terminal>

    public Terminal(String rdfId, String mRID, String name, String conductingEquipmentRdfResource, ConnectivityNode connectivityNode) {
        super(rdfId, mRID, name);
        this.conductingEquipmentRdfResource = new RdfResource(conductingEquipmentRdfResource);
        this.connectivityNodeRdfResource = new RdfResource(connectivityNode.getRdfId());
        this.connectivityNode = connectivityNode;
    }

    @XmlElement(name = "cim:Terminal.ConductingEquipment")
    private final RdfResource conductingEquipmentRdfResource;

    @XmlElement(name = "cim:Terminal.ConnectivityNode")
    private final RdfResource connectivityNodeRdfResource;

    @XmlTransient
    private final ConnectivityNode connectivityNode;

}
