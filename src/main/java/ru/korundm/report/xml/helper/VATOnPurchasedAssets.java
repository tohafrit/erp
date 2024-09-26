package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class VATOnPurchasedAssets {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "Выделено")
    private String highlighted = "0.00";

    @XmlAttribute(name = "ВключеноВСтоимостьЗапасов")
    private String includedInStockCost = "0.00";

    @XmlAttribute(name = "ПринятоКВычету")
    private String acceptedByDeduction = "0.00";
}