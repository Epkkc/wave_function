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
@XmlRootElement(name = "cim:ConformLoad")
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
                       Double activePowerMaxSummer, Double activePowerMaxWinter, Double reactivePowerMaxSummer, Double reactivePowerMaxWinter) {
        super(rdfId, mRID, name, baseVoltageRdfResource, normallyInService);
        this.activePowerFixed = activePowerFixed;
        this.reactivePowerFixed = reactivePowerFixed;
        this.activePowerMaxSummer = activePowerMaxSummer;
        this.activePowerMaxWinter = activePowerMaxWinter;
        this.reactivePowerMaxSummer = reactivePowerMaxSummer;
        this.reactivePowerMaxWinter = reactivePowerMaxWinter;
    }

    @XmlElement(name = "cim:EnergyConsumer.pfixed", required = true)
    private final Double activePowerFixed;

    @XmlElement(name = "cim:EnergyConsumer.qfixed", required = true)
    private final Double reactivePowerFixed;

    @XmlElement(name = "cim:ConformLoad.pMaxSummerLoad", required = true)
    private final Double activePowerMaxSummer;

    @XmlElement(name = "cim:ConformLoad.pMaxWinterLoad", required = true)
    private final Double activePowerMaxWinter;

    @XmlElement(name = "cim:ConformLoad.qMaxSummerLoad", required = true)
    private final Double reactivePowerMaxSummer;

    @XmlElement(name = "cim:ConformLoad.qMaxWinterLoad", required = true)
    private final Double reactivePowerMaxWinter;

    @XmlTransient
    private Terminal terminal;

}
