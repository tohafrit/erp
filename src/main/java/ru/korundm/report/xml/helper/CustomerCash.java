package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerCash {

    @XmlAttribute(name = "ЦенаКонтракта")
    private String contractPrice = "0.00";

    @XmlAttribute(name = "ПроцентВыполнения")
    private String percentageCompletion = "0.00";

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ВозвращеноЗаказчику")
    private String returnedCustomer = "0.00";

    @XmlAttribute(name = "ВозвращеноЗаказчикуСобственныеСредства")
    private String returnedCustomerOwnFunds = "0.00";

    @XmlAttribute(name = "ПолученоОтЗаказчика")
    private String receivedFromCustomer = "0.00";
}