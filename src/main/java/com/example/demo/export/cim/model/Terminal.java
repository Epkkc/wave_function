package com.example.demo.export.cim.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class Terminal extends BaseElement {

    public Terminal(String rdfId, String mRID, String name, String conductingEquipmentRdfResource, ConnectivityNode connectivityNode) {
        super(rdfId, mRID, name);
        this.conductingEquipmentRdfResource = new RdfResource(conductingEquipmentRdfResource);
        this.connectivityNodeRdfResource = new RdfResource(connectivityNode.getRdfId());
        this.connectivityNode = connectivityNode;
    }

    @XmlElement(name = "Terminal.ConductingEquipment", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    private RdfResource conductingEquipmentRdfResource;

    @XmlElement(name = "Terminal.ConnectivityNode", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    private RdfResource connectivityNodeRdfResource;

    @XmlTransient
    private ConnectivityNode connectivityNode;

}
