package ru.korundm.entity;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы назначений компонентов component_appointments
 * @author pakhunov_an
 * Date: 28.01.2020
 */
@Entity
@Table(name = "component_appointments")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class ComponentAppointment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Size(min = 1, max = 256)
    @Column(name = "name", unique = true, length = 256, nullable = false)
    private String name; // наименование

    @Column(name = "comment")
    private String comment; // комментарий
}

