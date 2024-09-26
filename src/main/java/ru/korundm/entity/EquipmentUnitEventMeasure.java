package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы мероприятий по событиям, произошедшим с единицей оборудования equipment_unit_event_measures
 * @author mazur_ea
 * Date: 26.02.2019
 */
@Entity
@Table(name = "equipment_unit_event_measures")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EquipmentUnitEventMeasure implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @ManyToOne
    @JoinColumn(name = "equipment_unit_event_id", nullable = false)
    private EquipmentUnitEvent equipmentUnitEvent; // событие

    @Column(name = "name", nullable = false)
    private String name; // наименование

    @Column(name = "commentary", nullable = false)
    private String commentary; // комментарий

    @Column(name = "start_on")
    private LocalDateTime startOn; // дата начала

    @Column(name = "end_on")
    private LocalDateTime endOn; // дата окончания

    @OneToMany(mappedBy = "equipmentUnitEventMeasure", cascade = CascadeType.ALL)
    private List<EquipmentUnitEventMeasureMaterial> equipmentUnitEventMeasureMaterialList = new ArrayList<>();
}