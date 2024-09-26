package ru.korundm.dto.decipherment;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Класс для хранения модели данных для работы с компонентами изделия, входящего в иерархию состава расшифровки при работе с расшифровками
 * @author mazur_ea
 * Date:   26.09.2019
 */

@EqualsAndHashCode(of = "componentId")
public final class DeciphermentDataComponent implements Serializable {

    private Long componentId; // идентификатор компонента (eco)
    private Long groupId; // идентификатор группы
    private String groupName; // наименование группы
    private String okpdCode; // код ОКП/ОКПД2
    private String cell; // группа/позиция компонента
    private String ecoName; // наименование по эко
    private String asuName; // наименование по АСУ (0 аналог)
    private Double quantity; // количество
    private String description; // описание
    private String unitMeasure; // единица измерения

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOkpdCode() {
        return okpdCode;
    }

    public void setOkpdCode(String okpdCode) {
        this.okpdCode = okpdCode;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getEcoName() {
        return ecoName;
    }

    public void setEcoName(String ecoName) {
        this.ecoName = ecoName;
    }

    public String getAsuName() {
        return asuName;
    }

    public void setAsuName(String asuName) {
        this.asuName = asuName;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnitMeasure() {
        return unitMeasure;
    }

    public void setUnitMeasure(String unitMeasure) {
        this.unitMeasure = unitMeasure;
    }
}