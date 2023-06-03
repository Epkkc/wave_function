package com.example.demo.export.cim.model;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.xml.bind.annotation.XmlAttribute;

@Getter
@RequiredArgsConstructor
public class RdfResource {

    @XmlAttribute(name = "rdf:resource", required = true)
    private final String rdfResource;

}
