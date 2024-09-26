package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "materialsInWarehouses",
        "vatOnPurchasedAssets",
        "prefabricatedInStocks",
        "materialsTransferredToRecycling",
        "futureSpending",
        "meansProduction"
})
public class GroupReserves {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "СформированоЗапасов")
    private String formedStocks = "0.00";

    @XmlAttribute(name = "СформированоЗапасовСредстваДругихКонтрактов")
    private String formedStocksMeansOtherContracts = "0.00";

    @XmlAttribute(name = "СформированоЗапасовСобственныеСредства")
    private String formedStocksOwnFunds = "0.00";

    @XmlAttribute(name = "ИспользованоЗапасов")
    private String usedStocks = "0.00";

    @XmlAttribute(name = "ИспользованоЗапасовНаДругиеКонтракты")
    private String usedStocksOnOtherContracts = "0.00";

    @XmlAttribute(name = "ИспользованоЗапасовНуждыОрганизации")
    private String usedStocksOrganizationNeeds = "0.00";

    @XmlElement(name = "МатериалыНаСкладах")
    private MaterialsInWarehouses materialsInWarehouses = new MaterialsInWarehouses();

    @XmlElement(name = "НДСПоПриобретеннымЦенностям")
    private VATOnPurchasedAssets vatOnPurchasedAssets = new VATOnPurchasedAssets();

    @XmlElement(name = "ПолуфабрикатыНаСкладах")
    private PrefabricatedInStocks prefabricatedInStocks = new PrefabricatedInStocks();

    @XmlElement(name = "МатериалыПереданныеВПереработку")
    private MaterialsTransferredToRecycling materialsTransferredToRecycling = new MaterialsTransferredToRecycling();

    @XmlElement(name = "РасходыБудущихПериодов")
    private FutureSpending futureSpending = new FutureSpending();

    @XmlElement(name = "СредстваПроизводства")
    private MeansProduction meansProduction = new MeansProduction();
}