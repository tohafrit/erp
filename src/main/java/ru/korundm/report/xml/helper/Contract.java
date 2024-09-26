package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;
import ru.korundm.report.xml.helper.adapter.LocalDateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

@Getter
@Setter
@XmlType(propOrder = {
        "groupFinancingContract",
        "groupDistributionContractResources",
        "groupShippingProductsPerformanceWorks",
        "redirectionAttraction",
        "writtenOffFunds"
})
@XmlAccessorType(XmlAccessType.FIELD)
public class Contract {

    @XmlAttribute(name = "ИГК")
    private String igk = "";

    @XmlAttribute(name = "НомерОтдельногоСчета")
    private String singleAccountNumber = "";

    @XmlAttribute(name = "ДатаСоставленияОтчета")
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    private LocalDate reportDate = LocalDate.now();

    @XmlAttribute(name = "НомерКонтракта")
    private String number = "";

    @XmlAttribute(name = "ДатаЗаключенияКонтракта")
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    private LocalDate contractDate = LocalDate.now();

    @XmlAttribute(name = "ПлановаяДатаИсполнения")
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    private LocalDate plannedExecutionDate = LocalDate.now();

    @XmlElement(name = "ГруппаФинансированиеКонтракта")
    private GroupFinancingContract groupFinancingContract = new GroupFinancingContract();

    @XmlElement(name = "ГруппаРаспределениеРесурсовКонтракта")
    private GroupDistributionContractResources groupDistributionContractResources = new GroupDistributionContractResources();

    @XmlElement(name = "ГруппаОтгрузкаПродукцииВыполнениеРабот")
    private GroupShippingProductsPerformanceWorks groupShippingProductsPerformanceWorks = new GroupShippingProductsPerformanceWorks();

    @XmlElement(name = "ПеренаправлениеПривлечение")
    private RedirectionAttraction redirectionAttraction = new RedirectionAttraction();

    @XmlElement(name = "СписаноСредств")
    private WrittenOffFunds writtenOffFunds = new WrittenOffFunds();
}