package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class SemifinishedInternalWorks {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "СписаноНаЗатраты")
    private String chargedToCosts = "0.00";
}