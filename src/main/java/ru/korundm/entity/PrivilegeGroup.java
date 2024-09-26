package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы групп доступов
 */
@Entity
@Table(name = "privilege_groups")
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class PrivilegeGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", length = 32, unique = true, nullable = false)
    private String name; // наименование

    @Column(name = "description", length = 512)
    private String description; // описание

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Privilege> privilegeList = new ArrayList<>(); // список доступов
}