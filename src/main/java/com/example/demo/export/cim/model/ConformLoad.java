package com.example.demo.export.cim.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class ConformLoad extends ServicedElement {

//      <cim:ConformLoad rdf:ID = "_CLoad_8">
//        <cim:ConductingEquipment.BaseVoltage rdf:resource="#_115"/>
//        <cim:Equipment.normallyInService>true</cim:Equipment.normallyInService>

//        <cim:EnergyConsumer.pfixed>13.5</cim:EnergyConsumer.pfixed>
//        <cim:EnergyConsumer.qfixed>5.8</cim:EnergyConsumer.qfixed>
//        <cim:ConformLoad.pMaxSummerLoad>13.5</cim:ConformLoad.pMaxSummerLoad>
//        <cim:ConformLoad.pMaxWinterLoad>13.9</cim:ConformLoad.pMaxWinterLoad>
//        <cim:ConformLoad.qMaxSummerLoad>5.8</cim:ConformLoad.qMaxSummerLoad>
//        <cim:ConformLoad.qMaxWinterLoad>6.2</cim:ConformLoad.qMaxWinterLoad>
//        <cim:EnergyConsumer.rate>1.1</cim:EnergyConsumer.rate>

//        <cim:IdentifiedObject.mRID>CLoad_8</cim:IdentifiedObject.mRID>
//        <cim:IdentifiedObject.name>Нагрузка в узле 8</cim:IdentifiedObject.name>
//        <nti:IdentifiedObject.projectID>rastrwin</nti:IdentifiedObject.projectID>
//      </cim:ConformLoad>


    public ConformLoad(String rdfId, String mRID, String name, String baseVoltageRdfResource, boolean normallyInService, Double activePowerFixed, Double reactivePowerFixed,
                       Double activePowerMaxSummer, Double activePowerMaxWinter, Double reactivePowerMaxSummer, Double reactivePowerMaxWinter, Terminal terminal) {
        super(rdfId, mRID, name, baseVoltageRdfResource, normallyInService);
        this.activePowerFixed = activePowerFixed;
        this.reactivePowerFixed = reactivePowerFixed;
        this.activePowerMaxSummer = activePowerMaxSummer;
        this.activePowerMaxWinter = activePowerMaxWinter;
        this.reactivePowerMaxSummer = reactivePowerMaxSummer;
        this.reactivePowerMaxWinter = reactivePowerMaxWinter;
        this.terminal = terminal;
    }

    @XmlElement(name = "EnergyConsumer.pfixed", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private Double activePowerFixed;

    @XmlElement(name = "EnergyConsumer.qfixed", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private Double reactivePowerFixed;

    @XmlElement(name = "ConformLoad.pMaxSummerLoad", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private Double activePowerMaxSummer;

    @XmlElement(name = "ConformLoad.pMaxWinterLoad", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private Double activePowerMaxWinter;

    @XmlElement(name = "ConformLoad.qMaxSummerLoad", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private Double reactivePowerMaxSummer;

    @XmlElement(name = "ConformLoad.qMaxWinterLoad", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private Double reactivePowerMaxWinter;

    @XmlTransient
    private Terminal terminal;



}
