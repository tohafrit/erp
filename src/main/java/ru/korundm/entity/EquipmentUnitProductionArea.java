package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность с описанием таблицы единиц оборудования на участках equipment_unit_production_areas
 * @author mazur_ea
 * Date: 18.01.2019
 */
@Entity
@Table(name = "equipment_unit_production_areas")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EquipmentUnitProductionArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_unit_id",  nullable = false)
    private EquipmentUnit equipmentUnit; // единица оборудования

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_area_id")
    private ProductionArea productionArea; // производственный учаток

    @Column(name = "moved_on", nullable = false)
    private LocalDateTime movedOn; // дата перемещения на участок
}