package com.example.demo.export.cim.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseElement {

    @XmlAttribute(name = "ID", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#", required = true)
    private String rdfId;

    @XmlElement(name = "IdentifiedObject.mRID", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private String mRID;

    @XmlElement(name = "IdentifiedObject.name", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    private String name;

    @XmlElement(name = "IdentifiedObject.projectID", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", required = true)
    private final String projectId = "didli_dudli";

}
