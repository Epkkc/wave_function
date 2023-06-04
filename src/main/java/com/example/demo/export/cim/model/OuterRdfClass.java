package com.example.demo.export.cim.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

//todo изучить namespace-ы у jaxb
@NoArgsConstructor
@Setter
@Getter
@XmlRootElement(name = "RDF", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class OuterRdfClass {
    List<PowerTransformer> powerTransformers;
    List<PowerTransformerEnd> powerTransformersEnds;
    List<ConformLoad> conformLoads;
    List<EquivalentInjection> equivalentInjections;
    List<BusBarSection> busBarSections;
    List<Terminal> terminals;
    List<ACLine> acLines;
    List<ConnectivityNode> connectivityNodes;
    List<BaseVoltage> baseVoltages;

    @XmlElement(name = "PowerTransformer", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    public List<PowerTransformer> getPowerTransformers() {
        return powerTransformers;
    }

    @XmlElement(name = "PowerTransformerEnd", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    public List<PowerTransformerEnd> getPowerTransformersEnds() {
        return powerTransformersEnds;
    }

    @XmlElement(name = "ConformLoad", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    public List<ConformLoad> getConformLoads() {
        return conformLoads;
    }

    @XmlElement(name = "EquivalentInjection", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    public List<EquivalentInjection> getEquivalentInjections() {
        return equivalentInjections;
    }

    @XmlElement(name = "BusbarSection", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    public List<BusBarSection> getBusBarSections() {
        return busBarSections;
    }

    @XmlElement(name = "Terminal", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    public List<Terminal> getTerminals() {
        return terminals;
    }

    @XmlElement(name = "ACLineSegment", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    public List<ACLine> getAcLines() {
        return acLines;
    }

    @XmlElement(name = "ConnectivityNode", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    public List<ConnectivityNode> getConnectivityNodes() {
        return connectivityNodes;
    }

    @XmlElement(name = "BaseVoltage", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    public List<BaseVoltage> getBaseVoltages() {
        return baseVoltages;
    }
}
