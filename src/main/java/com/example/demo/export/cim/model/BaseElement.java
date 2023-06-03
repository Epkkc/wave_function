package com.example.demo.export.cim.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@Data
@AllArgsConstructor
public class BaseElement {

    @XmlAttribute(name = "rdf:ID", required = true)
    private final String rdfId;

    @XmlElement(name = "cim:IdentifiedObject.mRID", required = true)
    private final String mRID;

    @XmlElement(name = "cim:IdentifiedObject.name")
    private final String name;

    @XmlElement(name = "cim:IdentifiedObject.projectID", required = true)
    private final String projectId = "didli_dudli";

}
