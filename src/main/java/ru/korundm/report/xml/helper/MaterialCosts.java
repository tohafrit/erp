package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class MaterialCosts {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ЦелевойПоказатель")
    private String targetIndicator = "0.00";

    @XmlAttribute(name = "ПроцентВыполнения")
    private String percentageCompletion = "0.00";

    @XmlAttribute(name = "СписаноНаЗатраты")
    private String chargedCosts = "0.00";

    @XmlAttribute(name = "СписаноЗатратДругихКонтрактов")
    private String chargedOtherContractCosts = "0.00";

    @XmlAttribute(name = "СписаноСобственныхЗатрат")
    private String chargedOwnCost = "0.00";

    @XmlAttribute(name = "ИсключеноИзЗатрат")
    private String excludedFromCosts = "0.00";

    @XmlAttribute(name = "ОтнесеноНаДругиеКонтракты")
    private String relatedToOtherContracts = "0.00";

    @XmlAttribute(name = "ОтнесеноНаСобственныеЗатраты")
    private String relatedToOwnCosts = "0.00";
}