package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class CostSales {

    @XmlAttribute(name = "ЦелевойПоказатель")
    private String targetIndicator = "0.00";

    @XmlAttribute(name = "ПроцентВыполнения")
    private String percentageCompletion = "0.00";

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "СебестоимостьКонтракт")
    private String costContract = "0.00";

    @XmlAttribute(name = "СебестоимостьНеКонтракт")
    private String costNonContract = "0.00";
}