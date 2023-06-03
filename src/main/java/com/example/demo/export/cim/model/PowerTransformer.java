package com.example.demo.export.cim.model;

import com.example.demo.base.model.enums.VoltageLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
import java.util.Map;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@XmlRootElement(name = "cim:PowerTransformer")
public class PowerTransformer extends ServicedElement {

//      <cim:PowerTransformer rdf:ID = "_PT_21_15">
//        <cim:ConductingEquipment.BaseVoltage rdf:resource="#_230"/>
//        <cim:Equipment.normallyInService>true</cim:Equipment.normallyInService>
//        <cim:IdentifiedObject.mRID>PT_21_15</cim:IdentifiedObject.mRID>
//        <cim:IdentifiedObject.name>Двухобм. ТР с узлами: 21 и 15</cim:IdentifiedObject.name>
//        <nti:IdentifiedObject.projectID>rastrwin</nti:IdentifiedObject.projectID>

//        <cim:ConductingEquipment.technicalReequipmentCost>13000000</cim:ConductingEquipment.technicalReequipmentCost>
//        <cim:ConductingEquipment.capitalRepairCost>5750000</cim:ConductingEquipment.capitalRepairCost>
//        <cim:ConductingEquipment.currentRepairCost>500000</cim:ConductingEquipment.currentRepairCost>
//        <cim:PowerTransformer.uK_HighLow>16.5</cim:PowerTransformer.uK_HighLow>
//        <cim:PowerTransformer.pK_HighLow>1.6</cim:PowerTransformer.pK_HighLow>
//      </cim:PowerTransformer>


    public PowerTransformer(String rdfId, String mRID, String name, String baseVoltageRdfResource, boolean normallyInService) {
        super(rdfId, mRID, name, baseVoltageRdfResource, normallyInService);
    }

    @XmlElement(name = "cim:ConductingEquipment.technicalReequipmentCost", required = true)
    private final Integer technicalReequipmentCost = 13000000;

    @XmlElement(name = "cim:ConductingEquipment.capitalRepairCost", required = true)
    private final Integer capitalRepairCost = 5750000;

    @XmlElement(name = "cim:ConductingEquipment.currentRepairCost", required = true)
    private final Integer currentRepairCost = 500000;

    @XmlElement(name = "cim:PowerTransformer.uK_HighLow", required = true)
    private final Double uKHighLow = 16.5;

    @XmlElement(name = "cim:PowerTransformer.pK_HighLow", required = true)
    private final Double pKHighLow = 1.6;

    @XmlTransient
    private Map<VoltageLevel, PowerTransformerEnd> ends;

    public void setEnds(Map<VoltageLevel, PowerTransformerEnd> ends) {
        this.ends = ends;
    }
}
