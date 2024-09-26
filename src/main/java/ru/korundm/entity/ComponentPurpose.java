package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы component_purposes
 * @author pakhunov_an
 * Date:   24.07.2020
 */
@Entity
@Table(name = "component_purposes")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ComponentPurpose implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "name", unique = true, nullable = false)
    private String name; // наименование

    @Column(name = "description", length = 512)
    private String description; // описание
}