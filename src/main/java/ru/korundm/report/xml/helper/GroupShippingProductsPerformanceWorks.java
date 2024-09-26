package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "costSales",
        "aur",
        "sellingCosts",
        "bankLoanInterest",
        "vatSales",
        "profit"
})
public class GroupShippingProductsPerformanceWorks {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ЦелевойПоказатель")
    private String targetIndicator = "0.00";

    @XmlAttribute(name = "ПроцентВыполнения")
    private String percentageCompletion = "0.00";

    @XmlElement(name = "СебестоимостьПродаж")
    private CostSales costSales = new CostSales();

    @XmlElement(name = "АУР")
    private Aur aur = new Aur();

    @XmlElement(name = "КоммерческиеРасходы")
    private SellingCosts sellingCosts = new SellingCosts();

    @XmlElement(name = "ПроцентыПоБанковскимКредитам")
    private BankLoanInterest bankLoanInterest = new BankLoanInterest();

    @XmlElement(name = "НДСПродажи")
    private VATSales vatSales = new VATSales();

    @XmlElement(name = "Прибыль")
    private Profit profit = new Profit();
}