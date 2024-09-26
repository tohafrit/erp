package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы материалов, использованных в мероприятиях по событиям, произошедших с единицей оборудования equipment_unit_event_measure_materials
 * @author mazur_ea
 * Date: 26.02.2019
 */
@Entity
@Table(name = "equipment_unit_event_measure_materials")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EquipmentUnitEventMeasureMaterial implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @ManyToOne
    @JoinColumn(name = "equipment_unit_event_measure_id", nullable = false)
    private EquipmentUnitEventMeasure equipmentUnitEventMeasure; // мероприятие

    @Column(name = "component", nullable = false)
    private String component; // компонент

    @Column(name = "quantity", nullable = false)
    private String quantity; // количество
}
