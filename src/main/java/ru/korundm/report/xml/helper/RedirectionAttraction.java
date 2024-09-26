package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class RedirectionAttraction {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ПривлеченоСредствДругихКонтрактов")
    private String attractedFundsOtherContracts = "0.00";

    @XmlAttribute(name = "ПривлеченоСобственныхСредств")
    private String attractedOwnFunds = "0.00";

    @XmlAttribute(name = "ИспользованоНаДругиеКонтракты")
    private String usedOnOtherContracts = "0.00";

    @XmlAttribute(name = "ИспользованоНаСобственныеНужды")
    private String usedForYourOwnNeeds = "0.00";
}