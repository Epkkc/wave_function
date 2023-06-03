package com.example.demo.export.cim.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ServicedElement extends BaseElement{

    public ServicedElement(String rdfId, String mRID, String name, String baseVoltageRdfResource, boolean normallyInService) {
        super(rdfId, mRID, name);
        this.baseVoltage = new RdfResource(baseVoltageRdfResource);
        this.normallyInService = normallyInService;
    }

    @XmlElement(name = "cim:ConductingEquipment.BaseVoltage")
    private final RdfResource baseVoltage;

    @XmlElement(name = "cim:Equipment.normallyInService")
    private final Boolean normallyInService;
}
