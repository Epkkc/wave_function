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
