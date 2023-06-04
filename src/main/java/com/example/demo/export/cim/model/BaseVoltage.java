package com.example.demo.export.cim.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseVoltage extends BaseElement {

//      <cim:BaseVoltage rdf:ID = "_115">
//        <cim:IdentifiedObject.mRID>115</cim:IdentifiedObject.mRID>
//        <cim:IdentifiedObject.name>115 кВ</cim:IdentifiedObject.name>
//        <nti:IdentifiedObject.projectID>rastrwin</nti:IdentifiedObject.projectID>
//        <cim:BaseVoltage.nominalVoltage>115.0</cim:BaseVoltage.nominalVoltage>
//      </cim:BaseVoltage>


    public BaseVoltage(String rdfId, String mRID, String name, Double nominalVoltage) {
        super(rdfId, mRID, name);
        this.nominalVoltage = nominalVoltage;
    }

    @XmlElement(name = "BaseVoltage.nominalVoltage", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    private Double nominalVoltage;

}
