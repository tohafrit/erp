package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class DebtPercentageCredits {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ПогашеноПроцентов")
    private String redeemedPercent = "0.00";

    @XmlAttribute(name = "ПогашеноПроцентовСобственныеСредства")
    private String redeemedInterestOwnFunds = "0.00";

    @XmlAttribute(name = "НачисленоПроцентов")
    private String accruedInterest = "0.00";
}