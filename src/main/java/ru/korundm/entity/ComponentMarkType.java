package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы component_mark_types
 * @author pakhunov_an
 * Date:   27.01.2020
 */
@Entity
@Table(name = "component_mark_types")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ComponentMarkType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @Column(name = "component_mark", nullable = false)
    private String mark; // обозначение
}