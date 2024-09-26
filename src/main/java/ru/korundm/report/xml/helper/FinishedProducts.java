package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class FinishedProducts {

    @XmlAttribute(name = "СальдоОпераций")
    private String balanceOperations = "0.00";

    @XmlAttribute(name = "Выпущено")
    private String released = "0.00";

    @XmlAttribute(name = "ИспользованоСДругихКонтрактов")
    private String usedFromOtherContracts = "0.00";

    @XmlAttribute(name = "ИспользованоСобственной")
    private String usedOwn = "0.00";

    @XmlAttribute(name = "Отгружено")
    private String shipped = "0.00";

    @XmlAttribute(name = "ОтгруженоНаДругиеКонтракты")
    private String shippedToOtherContracts = "0.00";

    @XmlAttribute(name = "ОтгруженоНаНуждыОрганизации")
    private String shippedToOrganizationNeeds = "0.00";
}