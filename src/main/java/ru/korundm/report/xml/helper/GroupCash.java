package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "cashEquitySeparateAccount",
        "bankDeposits",
        "advancesIssued"
})
public class GroupCash {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ДенежныеАктивы")
    private String cashAssets = "0.00";

    @XmlAttribute(name = "ДенежныеАктивыСредстваДругихКонтрактов")
    private String cashAssetsOtherContracts = "0.00";

    @XmlAttribute(name = "ДенежныеАктивыСобственныеСредства")
    private String cashAssetsOwnFunds = "0.00";

    @XmlAttribute(name = "ИспользованиеРесурсов")
    private String resourceUsage = "0.00";

    @XmlAttribute(name = "ИспользованиеРесурсовДругиеКонтракты")
    private String useResourcesOtherContracts = "0.00";

    @XmlAttribute(name = "ИспользованиеРесурсовСобственныеСредства")
    private String useResourcesOwnFunds = "0.00";

    @XmlElement(name = "ДенежныеСредстваОтдельныйСчет")
    private CashEquitySeparateAccount cashEquitySeparateAccount = new CashEquitySeparateAccount();

    @XmlElement(name = "БанковскиеДепозиты")
    private BankDeposits bankDeposits = new BankDeposits();

    @XmlElement(name = "АвансыВыданные")
    private AdvancesIssued advancesIssued = new AdvancesIssued();
}