package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы employees (ex. tab_sotrud)
 * @author surov_pv
 * Date:   11.04.2018
 */
@Entity
@Table(name = "employees")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "personnel_number")
    private Integer personnelNumber; // табельный номер

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_area_id")
    private ProductionArea productionArea; // участок

    @Column(name = "status")
    private Integer status; // статус (возможные значения - EmployeeStatusState.java)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plant plant; // предприятие

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // пользователь

    @Column(name = "first_name")
    private String firstName; // имя

    @Column(name = "surname")
    private String surname; // отчество

    @Column(name = "second_name")
    private String secondName; // фамилия
}