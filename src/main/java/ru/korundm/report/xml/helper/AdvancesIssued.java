package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class AdvancesIssued {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "АвансыИсполнениеКонтракта")
    private String advancesContractExecution = "0.00";

    @XmlAttribute(name = "АвансыСредстваДругихКонтрактов")
    private String advancesOtherContracts = "0.00";

    @XmlAttribute(name = "АвансыСобственныеСредства")
    private String advancesOwnFunds = "0.00";

    @XmlAttribute(name = "ЗачтеноАвансов")
    private String creditAdvances = "0.00";

    @XmlAttribute(name = "СписаноЗадолженностиКооперации")
    private String chargedDebtCooperation = "0.00";
}