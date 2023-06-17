package com.example.demo.export.cim.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class BusBarSection extends ServicedElement {

    public BusBarSection(String rdfId, String mRID, String name, String baseVoltageRdfResource, boolean normallyInService, Terminal terminal) {
        super(rdfId, mRID, name, baseVoltageRdfResource, normallyInService);
        this.terminal = terminal;
    }

    @XmlTransient
    private Terminal terminal;


}
