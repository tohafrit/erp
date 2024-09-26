package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы ролей
 */
@Entity
@Table(name = "roles")
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", length = 32, unique = true, nullable = false)
    private String name; // наименование

    @Column(name = "description", length = 512)
    private String description; // описание

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "role_privileges",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    private List<Privilege> privilegeList = new ArrayList<>(); // список доступов

    @ManyToMany(mappedBy = "roleList", cascade = CascadeType.ALL)
    private List<User> userList = new ArrayList<>(); // список пользователей
}