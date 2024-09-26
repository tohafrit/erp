package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы типов событий для единицы оборудования equipment_unit_event_types
 * @author mazur_ea
 * Date: 07.02.2019
 */
@Entity
@Table(name = "equipment_unit_event_types")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EquipmentUnitEventType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Size(min = 1, max = 128)
    @Column(name = "name", length = 128, unique = true, nullable = false)
    private String name; // наименование
}
