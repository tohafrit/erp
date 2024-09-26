package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы типов оборудования equipment_types
 * @author mazur_ea
 * Date: 18.01.2019
 */
@Entity
@Table(name = "equipment_types")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EquipmentType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @NotNull
    @Column(name = "code", unique = true, nullable = false)
    private Integer code; // код

    @Size(min = 1)
    @Column(name = "name", unique = true, nullable = false)
    private String name; // наименование

    @Size(min = 1)
    @Column(name = "description", nullable = false)
    private String description; // описание
}
