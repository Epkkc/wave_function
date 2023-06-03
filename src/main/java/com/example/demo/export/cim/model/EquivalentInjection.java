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
@XmlRootElement(name = "cim:EquivalentInjection")
public class EquivalentInjection extends ServicedElement {

//      <cim:EquivalentInjection rdf:ID = "_EqInj_1">
//        <cim:ConductingEquipment.BaseVoltage rdf:resource="#_243.8"/>
//        <cim:Equipment.normallyInService>true</cim:Equipment.normallyInService>
//        <cim:IdentifiedObject.mRID>EqInj_1</cim:IdentifiedObject.mRID>
//        <cim:IdentifiedObject.name>Генерация в узле 1</cim:IdentifiedObject.name>
//        <nti:IdentifiedObject.projectID>rastrwin</nti:IdentifiedObject.projectID>
//        <cim:EquivalentInjection.p>217.311</cim:EquivalentInjection.p>
//        <cim:EquivalentInjection.q>122.88561</cim:EquivalentInjection.q>
//        <nti:EquivalentInjection.injectorType rdf:resource="http://iec.ch/TC57/2013/CIM-schema-cim16#InjectorType.generator"/>
//      </cim:EquivalentInjection>

    public EquivalentInjection(String rdfId, String mRID, String name, String baseVoltageRdfResource, boolean normallyInService, Double activePower, Double reactivePower) {
        super(rdfId, mRID, name, baseVoltageRdfResource, normallyInService);
        this.activePower = activePower;
        this.reactivePower = reactivePower;
    }

    @XmlElement(name = "cim:EquivalentInjection.p", required = true)
    private final Double activePower;

    @XmlElement(name = "cim:EquivalentInjection.q", required = true)
    private final Double reactivePower;

    @XmlElement(name = "nti:EquivalentInjection.injectorType")
    private final RdfResource injectorType = new RdfResource("http://iec.ch/TC57/2013/CIM-schema-cim16#InjectorType.generator");

    @XmlTransient
    private Terminal terminal;

}
