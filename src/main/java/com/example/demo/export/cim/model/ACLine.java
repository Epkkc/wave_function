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
