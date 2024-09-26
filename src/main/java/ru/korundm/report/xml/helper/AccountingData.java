package ru.korundm.report.xml.helper;

import lombok.Getter;
import lombok.Setter;
import ru.korundm.report.xml.helper.adapter.LocalDateTimeAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlRootElement(
        name = "ДанныеРаздельногоУчета",
        namespace = "http://mil.ru/discreteAccounting"
)
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountingData {

    @XmlAttribute(name = "ИННОрганизации")
    private String inn = "7725700394";

    @XmlAttribute(name = "НаименованиеОрганизации")
    private String name = "Акционерное общество";

    @XmlAttribute(name = "КППОрганизации")
    private String kpp = "772601001";

    @XmlAttribute(name = "ДатаФормирования")
    @XmlJavaTypeAdapter(value = LocalDateTimeAdapter.class)
    private LocalDateTime createdOn = LocalDateTime.now().withNano(0);

    @XmlAttribute(name = "ГенераторОтчета")
    private String generator = "ERP";

    @XmlElement(name = "Контракт")
    private List<Contract> contractList = new ArrayList<>();
}