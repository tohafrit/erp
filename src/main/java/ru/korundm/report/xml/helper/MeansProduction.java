package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class MeansProduction {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ПоступилоСредствПроизводства")
    private String receivedMeansProduction = "0.00";

    @XmlAttribute(name = "ПоступилоСредствПроизводстваСредстваДругихКонтрактов")
    private String receivedMeansProductionMeansOtherContracts = "0.00";

    @XmlAttribute(name = "ПоступилоСредствПроизводстваСобственныеСредства")
    private String receivedMeansProductionOwnFunds = "0.00";

    @XmlAttribute(name = "ВыбылоСредствПроизводства")
    private String retiredProductionTools = "0.00";

    @XmlAttribute(name = "ВыбылоСредствПроизводстваНаДругиеКонтракты")
    private String retiredProductionMeansForOtherContracts = "0.00";

    @XmlAttribute(name = "ВыбылоСредствПроизводстваНуждыОрганизации")
    private String retiredProductionMeansNeedsOrganization = "0.00";
}