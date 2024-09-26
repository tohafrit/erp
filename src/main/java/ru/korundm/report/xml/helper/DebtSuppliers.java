package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class DebtSuppliers {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ОплаченоПоставщикам")
    private String paidSuppliers = "0.00";

    @XmlAttribute(name = "ОплаченоПоставщикамСредстваДругихКонтрактов")
    private String paidSuppliersOtherContracts = "0.00";

    @XmlAttribute(name = "ОплаченоПоставщикамСобственныеСредства")
    private String paidSuppliersOwnFunds = "0.00";

    @XmlAttribute(name = "СуммарнаяЗадолженность")
    private String totalDebt = "0.00";
}