package com.example.demo.export.cim.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
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
        addTerminals(terminals);
    }

    @XmlElement(name = "ConductingEquipment.technicalReequipmentCost", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private final Integer technicalReequipmentCost = 10000000;
    @XmlElement(name = "ConductingEquipment.capitalRepairCost", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private final Integer capitalRepairCost = 4750000;
    @XmlElement(name = "ConductingEquipment.currentRepairCost",namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private final Integer currentRepairCost = 300000;

    @XmlElement(name = "Conductor.length", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private Double length;

    @XmlElement(name = "ACLineSegment.bch", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private final Double bch = -24.2;

    @XmlElement(name = "ACLineSegment.r", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private Double r;

    @XmlElement(name = "ACLineSegment.x", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private Double x;

    @XmlElement(name = "ACLineSegment.rPerLength", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private final Double rPerLength = 0.165;

    @XmlElement(name = "ACLineSegment.xPerLength", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private final Double xPerLength = 0.42;

    @XmlTransient
    private final List<Terminal> terminals = new ArrayList<>();

    public void addTerminal(Terminal terminal){
        terminals.add(terminal);
    }

    public void addTerminals(Collection<Terminal> terminals){
        this.terminals.addAll(terminals);
    }

}
