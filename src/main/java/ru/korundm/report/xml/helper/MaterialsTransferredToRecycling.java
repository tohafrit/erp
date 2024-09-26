package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class MaterialsTransferredToRecycling {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ПереданоСтороннемуИсполнителю")
    private String submittedThirdPartyContractor = "0.00";

    @XmlAttribute(name = "ПринятоИзПереработки")
    private String adoptedFromRecycling = "0.00";

    @XmlAttribute(name = "ПринятоИзПереработкиНуждыОрганизации")
    private String adoptedFromOrganizationRecycling = "0.00";
}