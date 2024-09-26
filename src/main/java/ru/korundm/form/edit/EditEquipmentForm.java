package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.entity.EquipmentType;
import ru.korundm.entity.Producer;
import ru.korundm.entity.User;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class EditEquipmentForm implements Validatable {

    private Long id; // идентификатор
    private EquipmentType equipmentType; // тип
    private boolean archive; // архивность
    private String name; // наименование
    private User employee; // сотрудник
    private Integer shift; // сменность
    private Producer producer; // производитель
    private String model; // модель
    private Integer weight; // масса
    private String voltage; // напряжение
    private Integer power; // мощность
    private String dimensionsLength; // габариты - длина
    private String dimensionsDepth; // габариты - глубина
    private String dimensionsWidth; // габариты - ширина
    private String compressedAirPressure; // сжатый воздух - давление
    private String compressedAirConsumption; // сжатый воздух - расход
    private String nitrogenPressure; // азот
    private String water; // вода (WaterType.java)
    private boolean sewerage; // канализация
    private String extractorVolume; // вытяжка - объем
    private String extractorDiameter; // вытяжка - диаметр
    private String link; // документация
    private List<EditEquipmentUnitForm> equipmentUnitList = new ArrayList<>(); // список параметров единиц оборудования

    @Getter @Setter
    // Форма для единицы оборудования
    public static class EditEquipmentUnitForm {
        private Long id; // идентификатор
        private Long areaId; // идентификатор участка
        private String serialNumber; // серийный номер
        private String inventoryNumber; // инвентарный номер
        private String code; // код
    }

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(getName())) {
            errors.putError("name", ValidatorMsg.REQUIRED);
        }
    }
}