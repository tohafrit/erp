package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class CashEquitySeparateAccount {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ЗачисленоИсполнениеКонтракта")
    private String creditedContractExecution = "0.00";

    @XmlAttribute(name = "ЗачисленоИное")
    private String creditedOther = "0.00";

    @XmlAttribute(name = "СписаноИсполнениеКонтракта")
    private String writtenOffContractExecution = "0.00";

    @XmlAttribute(name = "СписаноДругиеКонтракты")
    private String writtenOffOtherContracts = "0.00";

    @XmlAttribute(name = "СписаноРасходыОрганизации")
    private String chargedOrganizationCosts = "0.00";
}