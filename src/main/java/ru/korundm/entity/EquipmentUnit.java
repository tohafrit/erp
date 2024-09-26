package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.util.CommonUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы единиц оборудования equipment_units
 * @author mazur_ea
 * Date: 18.01.2019
 */
@Entity
@Table(name = "equipment_units")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EquipmentUnit implements Serializable {

    /** Длина кода */
    public static final int LENGTH_CODE = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment; // оборудование

    @Column(name = "inventory_number")
    private String inventoryNumber; // инвентарный номер

    @Column(name = "serial_number")
    private String serialNumber; // серийный номер

    @ManyToOne(fetch = FetchType.LAZY)
    @org.hibernate.annotations.JoinFormula("(SELECT eupa.id FROM equipment_unit_production_areas eupa WHERE eupa.equipment_unit_id = id ORDER BY eupa.moved_on DESC LIMIT 1)")
    private EquipmentUnitProductionArea lastEquipmentUnitProductionArea; // последний переход на участок

    @OneToMany(mappedBy = "equipmentUnit", cascade = CascadeType.ALL)
    private List<EquipmentUnitProductionArea> equipmentUnitProductionAreaList = new ArrayList<>(); // список переходов на производственные участки

    @OneToMany(mappedBy = "equipmentUnit", cascade = CascadeType.ALL)
    private List<EquipmentUnitEvent> equipmentUnitEventList = new ArrayList<>(); // список событий с единицей оборудования

    @Column(name = "code", nullable = false)
    private Integer code = 1; // код

    /**
     * Метод формирует уникальный код оборудования
     * @return код оборудования
     */
    public String getUnitCode() {
        String result = "";
        EquipmentUnitProductionArea lastArea = getLastEquipmentUnitProductionArea();
        if (lastArea != null) {
            ProductionArea prodArea = lastArea.getProductionArea();
            if (prodArea != null) {
                result = prodArea.getFormatCode() + getEquipment().getEquipmentType().getCode() + CommonUtil.formatZero(String.valueOf(getCode()), LENGTH_CODE);
            }
        }
        return result;
    }
}