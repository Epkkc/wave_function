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
@XmlRootElement(name = "cim:PowerTransformerEnd")
public class PowerTransformerEnd extends ServicedElement {


//      <cim:PowerTransformerEnd rdf:ID = "_PTE_21_15">
//        <cim:TransformerEnd.BaseVoltage rdf:resource="#_230"/>
//        <cim:IdentifiedObject.mRID>PTE_21_15</cim:IdentifiedObject.mRID>
//        <nti:IdentifiedObject.projectID>rastrwin</nti:IdentifiedObject.projectID>

//        <cim:TransformerEnd.Terminal rdf:resource="#_T_CN_21_PTE_21_15"/>
//        <cim:PowerTransformerEnd.PowerTransformer rdf:resource="#_PT_21_15"/>
//        <cim:TransformerEnd.endNumber>1</cim:TransformerEnd.endNumber>
//        <cim:PowerTransformerEnd.b>1.0E-4</cim:PowerTransformerEnd.b>
//        <cim:PowerTransformerEnd.r>1.0E-4</cim:PowerTransformerEnd.r>
//        <cim:PowerTransformerEnd.x>276.2604</cim:PowerTransformerEnd.x>
//        <cim:PowerTransformerEnd.ratedS>33.51005</cim:PowerTransformerEnd.ratedS>
//      </cim:PowerTransformerEnd>


    public PowerTransformerEnd(String rdfId, String mRID, String name, String baseVoltageRdfResource, boolean normallyInService, String terminalRdfResource,
                               String powerTransformerRdfResource, Integer endNumber, Terminal terminal, PowerTransformer powerTransformer) {
        super(rdfId, mRID, name, baseVoltageRdfResource, normallyInService);
        this.terminalRdfResource = new RdfResource(terminalRdfResource);
        this.powerTransformerRdfResource = new RdfResource(powerTransformerRdfResource);
        this.endNumber = endNumber;
        this.terminal = terminal;
        this.powerTransformer = powerTransformer;
    }

    @XmlElement(name = "cim:TransformerEnd.Terminal", required = true)
    private final RdfResource terminalRdfResource;

    @XmlElement(name = "cim:PowerTransformerEnd.PowerTransformer", required = true)
    private final RdfResource powerTransformerRdfResource;

    @XmlElement(name = "cim:TransformerEnd.endNumber", required = true)
    private final Integer endNumber;

    @XmlElement(name = "cim:TransformerEnd.b", required = true)
    private final Double b = 0.0001;

    @XmlElement(name = "cim:TransformerEnd.r", required = true)
    private final Double r = 0.0001;

    @XmlElement(name = "cim:TransformerEnd.x", required = true)
    private final Double x = 276.2604;

    @XmlElement(name = "cim:PowerTransformerEnd.ratedS", required = true)
    private final Double ratedS = 33.51005;

    @XmlTransient
    private final Terminal terminal;

    @XmlTransient
    private final PowerTransformer powerTransformer;
}
