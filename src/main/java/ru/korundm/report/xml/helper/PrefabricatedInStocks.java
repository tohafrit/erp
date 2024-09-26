package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class PrefabricatedInStocks {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ПоступилоПолуфабрикатов")
    private String receivedSemiFinishedProducts = "0.00";

    @XmlAttribute(name = "ПоступилоПолуфабрикатовСредстваДругихКонтрактов")
    private String receivedSemiFinishedProductsMeansOtherContracts = "0.00";

    @XmlAttribute(name = "ПоступилоПолуфабрикатовСобственныеСредства")
    private String receivedSemiFinishedOwnMeans = "0.00";

    @XmlAttribute(name = "ИспользованоПолуфабрикатов")
    private String usedSemiFinishedProducts = "0.00";

    @XmlAttribute(name = "ИспользованоПолуфабрикатовНаДругиеКонтракты")
    private String usedSemiManufacturedForOtherContracts = "0.00";

    @XmlAttribute(name = "ИспользованоПолуфабрикатовНуждыОрганизации")
    private String usedSemiManufacturedNeedsOrganizations = "0.00";
}