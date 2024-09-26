package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "materialCosts",
        "payrollCosts",
        "otherProductionCosts",
        "overheadCost",
        "generalBusinessCosts",
        "semifinishedInternalWorks",
        "productionInnerProducts",
        "output"
})
public class GroupProduction {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ПроизводственныеЗатраты")
    private String productionCosts = "0.00";

    @XmlAttribute(name = "ПроизводственныеЗатратыДругихКонтрактов")
    private String productionCostsOtherContracts = "0.00";

    @XmlAttribute(name = "ПроизводственныеЗатратыСобственные")
    private String productionCostsOwn = "0.00";

    @XmlAttribute(name = "Выпуск")
    private String release = "0.00";

    @XmlAttribute(name = "ВыпускНаДругиеКонтракты")
    private String issueOnOtherContracts = "0.00";

    @XmlAttribute(name = "ВыпускНуждыОрганизации")
    private String organizationNeedsIssue = "0.00";

    @XmlElement(name = "МатериальныеЗатраты")
    private MaterialCosts materialCosts = new MaterialCosts();

    @XmlElement(name = "ЗатратыФОТ")
    private PayrollCosts payrollCosts = new PayrollCosts();

    @XmlElement(name = "ПрочиеПроизводственныеЗатраты")
    private OtherProductionCosts otherProductionCosts = new OtherProductionCosts();

    @XmlElement(name = "ОбщепроизводственныеЗатраты")
    private OverheadCost overheadCost = new OverheadCost();

    @XmlElement(name = "ОбщехозяйственныеЗатраты")
    private GeneralBusinessCosts generalBusinessCosts = new GeneralBusinessCosts();

    @XmlElement(name = "ПолуфабрикатыВнутренниеРаботы")
    private SemifinishedInternalWorks semifinishedInternalWorks = new SemifinishedInternalWorks();

    @XmlElement(name = "ВыпускПолуфабрикатовВнутреннихРабот")
    private ProductionInnerProducts productionInnerProducts = new ProductionInnerProducts();

    @XmlElement(name = "ВыпускПродукции")
    private Output output = new Output();
}