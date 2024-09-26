package ru.korundm.dto.launch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class LaunchProductStructureItem implements Serializable {

    private Long id;
    private String groupName;
    private String conditionalName; // условное наименование
    private int quantity; // количество

    private long balanceB1; // остаток Б1
    private long residueReserve; // Оостаток задела
    private long otherProducts; // изделия в составе остатка задела других изделий
    private long unLaunched; // претенденты
    private long forContract; // по договору
    private long unallotted; // Б1
    private long reserve; // в задел
    private long amount; // итого
    private long inReserve; // остаток задела
    private long usedForOther; // запускаются в составе других изделий
    private long amountToLaunch; // итого к запуску
    private long fromReserveTotal; // использование заделов предыдущих запусков
    private long totalPurchaseProductCount; // итого к закупке
    private long amountToBuy; // итого к закупке
    @JsonProperty("_children")
    private List<LaunchProductStructureItem> childList;
}