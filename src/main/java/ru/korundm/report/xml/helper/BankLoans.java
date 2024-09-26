package ru.korundm.report.xml.helper;


import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class BankLoans {

    @XmlAttribute(name = "ПлановыйОбъемКредитования")
    private String plannedVolumeCrediting = "0.00";

    @XmlAttribute(name = "ПроцентВыполнения")
    private String percentageCompletion = "0.00";

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ПогашеноТелаКредита")
    private String redeemedBodyCredit = "0.00";

    @XmlAttribute(name = "ПогашеноТелаКредитаСобственныеСредства")
    private String redeemedCreditCreditsOwnFunds = "0.00";

    @XmlAttribute(name = "ПривлеченоКредитов")
    private String attractedCredits = "0.00";
}