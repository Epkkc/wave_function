package com.example.demo.export.cim.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@XmlRootElement(name = "cim:ACLineSegment")
public class ACLine extends ServicedElement {

//      <cim:ACLineSegment rdf:ID = "_3_19">
//        <cim:ConductingEquipment.BaseVoltage rdf:resource="#_230"/>
//        <cim:Equipment.normallyInService>true</cim:Equipment.normallyInService>
//        <cim:IdentifiedObject.mRID>3_19</cim:IdentifiedObject.mRID>
//        <cim:IdentifiedObject.name>ЛЭП 3_19</cim:IdentifiedObject.name>
//        <nti:IdentifiedObject.projectID>rastrwin</nti:IdentifiedObject.projectID>

//        <cim:ConductingEquipment.technicalReequipmentCost>10000000</cim:ConductingEquipment.technicalReequipmentCost>
//        <cim:ConductingEquipment.capitalRepairCost>4750000</cim:ConductingEquipment.capitalRepairCost>
//        <cim:ConductingEquipment.currentRepairCost>300000</cim:ConductingEquipment.currentRepairCost>
//        <cim:Conductor.length>215.4</cim:Conductor.length>
//        <cim:ACLineSegment.bch>-24.2</cim:ACLineSegment.bch>
//        <cim:ACLineSegment.r>35.4483</cim:ACLineSegment.r>
//        <cim:ACLineSegment.x>90.47</cim:ACLineSegment.x>
//        <cim:ACLineSegment.rPerLength>0.165</cim:ACLineSegment.rPerLength>
//        <cim:ACLineSegment.lPerLength>0.42</cim:ACLineSegment.lPerLength>
//      </cim:ACLineSegment>


    public ACLine(String rdfId, String mRID, String name, String baseVoltageRdfResource, boolean normallyInService, Double length, List<Terminal> terminals) {
        super(rdfId, mRID, name, baseVoltageRdfResource, normallyInService);
        this.length = length;
        this.r = length * rPerLength;
        this.x = length * xPerLength;
        this.terminals = terminals;
    }

    @XmlElement(name = "cim:ConductingEquipment.technicalReequipmentCost", required = true)
    private final Integer technicalReequipmentCost = 10000000;
    @XmlElement(name = "cim:ConductingEquipment.capitalRepairCost", required = true)
    private final Integer capitalRepairCost = 4750000;
    @XmlElement(name = "cim:ConductingEquipment.currentRepairCost", required = true)
    private final Integer currentRepairCost = 300000;

    @XmlElement(name = "cim:Conductor.length", required = true)
    private final Double length;

    @XmlElement(name = "cim:ACLineSegment.bch", required = true)
    private final Double bch = -24.2;

    @XmlElement(name = "cim:ACLineSegment.r", required = true)
    private final Double r;

    @XmlElement(name = "cim:ACLineSegment.x", required = true)
    private final Double x;

    @XmlElement(name = "cim:ACLineSegment.rPerLength", required = true)
    private final Double rPerLength = 0.165;

    @XmlElement(name = "cim:ACLineSegment.xPerLength", required = true)
    private final Double xPerLength = 0.42;

    @XmlTransient
    private final List<Terminal> terminals;

}
