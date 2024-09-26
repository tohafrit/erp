package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы plants (ex. tab_prinadl)
 * @author surov_pv
 * Date:   30.03.2018
 */
@Entity
@Table(name = "plants")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class Plant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "code", unique = true, nullable = false)
    private Integer code; // код

    @Column(name = "code_name", unique = true, nullable = false)
    private Integer codeName; // код названия

    @Column(name = "name", unique = true, nullable = false)
    private String name; // название предприятия

    @Column(name = "letter_designation", nullable = false)
    private String letterDesignation; // буквенное обозначение
}