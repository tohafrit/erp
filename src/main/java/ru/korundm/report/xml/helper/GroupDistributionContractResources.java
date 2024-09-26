package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "groupCash",
        "groupReserves",
        "groupProduction",
        "finishedProducts"
})
public class GroupDistributionContractResources {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlElement(name = "ГруппаДенежныеСредства")
    private GroupCash groupCash = new GroupCash();

    @XmlElement(name = "ГруппаЗапасы")
    private GroupReserves groupReserves = new GroupReserves();

    @XmlElement(name = "ГруппаПроизводство")
    private GroupProduction groupProduction = new GroupProduction();

    @XmlElement(name = "ГотоваяПродукция")
    private FinishedProducts finishedProducts = new FinishedProducts();
}