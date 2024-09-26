package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class MaterialsInWarehouses {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "ПоступилоМатериалов")
    private String receivedMaterials = "0.00";

    @XmlAttribute(name = "ПоступилоМатериаловСредстваДругихКонтрактов")
    private String materialsReceivedMeansOtherContracts = "0.00";

    @XmlAttribute(name = "ПоступилоМатериаловСобственныеСредства")
    private String receivedMaterialsOwnFunds = "0.00";

    @XmlAttribute(name = "ИспользованоМатериалов")
    private String usedMaterials = "0.00";

    @XmlAttribute(name = "ИспользованоМатериаловНаДругиеКонтракты")
    private String materialsUsedOnOtherContracts = "0.00";

    @XmlAttribute(name = "ИспользованоМатериаловНуждыОрганизации")
    private String usedMaterialsNeedsOrganization = "0.00";
}