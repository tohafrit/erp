package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы групп компонентов component_groups (ex. tab_grupcom)
 * @author mazur_ea
 * Date: 12.03.2019
 */
@Entity
@Table(name = "component_groups")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ComponentGroup implements Serializable {

    public ComponentGroup() {}

    public ComponentGroup(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "number", unique = true, precision = 3, nullable = false)
    private Integer number; // номер

    @Column(name = "name", unique = true, length = 128, nullable = false)
    private String name; // наименование

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}