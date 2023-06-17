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
public class EquivalentInjection extends ServicedElement {

    public EquivalentInjection(String rdfId, String mRID, String name, String baseVoltageRdfResource, boolean normallyInService, Double activePower, Double reactivePower, Terminal terminal) {
        super(rdfId, mRID, name, baseVoltageRdfResource, normallyInService);
        this.activePower = activePower;
        this.reactivePower = reactivePower;
        this.terminal = terminal;
    }

    @XmlElement(name = "EquivalentInjection.p", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private Double activePower;

    @XmlElement(name = "EquivalentInjection.q", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private Double reactivePower;

    @XmlElement(name = "EquivalentInjection.injectorType", namespace = "http://nti.mpei.ru/#")
    private final RdfResource injectorType = new RdfResource("http://iec.ch/TC57/2013/CIM-schema-cim16#InjectorType.generator");

    @XmlTransient
    private Terminal terminal;

}
