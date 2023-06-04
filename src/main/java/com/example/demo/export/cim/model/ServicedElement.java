package com.example.demo.export.cim.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class ServicedElement extends BaseElement{

    public ServicedElement(String rdfId, String mRID, String name, String baseVoltageRdfResource, boolean normallyInService) {
        super(rdfId, mRID, name);
        this.baseVoltage = new RdfResource(baseVoltageRdfResource);
        this.normallyInService = normallyInService;
    }

//    @XmlElement(name = "cim:ConductingEquipment.BaseVoltage")
    @XmlElement(name = "ConductingEquipment.BaseVoltage", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    private RdfResource baseVoltage;

    @XmlElement(name = "Equipment.normallyInService", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    private Boolean normallyInService;
}
