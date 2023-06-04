
@XmlSchema(
    elementFormDefault = XmlNsForm.QUALIFIED,
    xmlns = {
        @XmlNs(prefix="cim", namespaceURI="http://iec.ch/TC57/2013/CIM-schema-cim16#"),
        @XmlNs(prefix="nti", namespaceURI="http://nti.mpei.ru/#"),
        @XmlNs(prefix="rdf", namespaceURI="http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
    }
)
package com.example.demo.export.cim.model;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;


