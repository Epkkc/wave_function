package com.example.demo.export.cim.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@XmlRootElement(name = "cim:BusbarSection")
public class BusBarSection extends ServicedElement {

//      <cim:BusbarSection rdf:ID = "_BS_CN_1">
//        <cim:ConductingEquipment.BaseVoltage rdf:resource="#_243.8"/>
//        <cim:Equipment.normallyInService>true</cim:Equipment.normallyInService>
//        <cim:IdentifiedObject.mRID>BS_CN_1</cim:IdentifiedObject.mRID>
//        <cim:IdentifiedObject.name>Шина в узле CN_1 ном. напряжением 243,8+ кВ</cim:IdentifiedObject.name>
//        <nti:IdentifiedObject.projectID>rastrwin</nti:IdentifiedObject.projectID>
//      </cim:BusbarSection>

    public BusBarSection(String rdfId, String mRID, String name, String baseVoltageRdfResource, boolean normallyInService, Terminal terminal) {
        super(rdfId, mRID, name, baseVoltageRdfResource, normallyInService);
        this.terminal = terminal;
    }

    @XmlTransient
    private final Terminal terminal;

}
