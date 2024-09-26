package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "customerCash",
        "bankLoans",
        "debtPercentageCredits",
        "debtSuppliers"
})
public class GroupFinancingContract {

    @XmlAttribute(name = "ЦелевойОбъемФинансирования")
    private String targetAmountFunding = "0.00";

    @XmlAttribute(name = "ПроцентВыполнения")
    private String percentageCompletion = "0.00";

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlElement(name = "ЗадолженностьПоПроцентамКредитов")
    private DebtPercentageCredits debtPercentageCredits = new DebtPercentageCredits();

    @XmlElement(name = "ДенежныеСредстваЗаказчика")
    private CustomerCash customerCash = new CustomerCash();

    @XmlElement(name = "ЗадолженностьПоставщикам")
    private DebtSuppliers debtSuppliers = new DebtSuppliers();

    @XmlElement(name = "БанковскиеКредиты")
    private BankLoans bankLoans = new BankLoans();
}