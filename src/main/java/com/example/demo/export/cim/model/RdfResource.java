package com.example.demo.export.cim.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class RdfResource {
    public RdfResource(String rdfResource) {
        this.rdfResource = "#" + rdfResource;
    }

    @XmlAttribute(name = "rdf:resource", required = true)
    private String rdfResource;

}
