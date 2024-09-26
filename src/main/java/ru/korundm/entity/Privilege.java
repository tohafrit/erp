package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы доступов
 */
@Entity
@Table(name = "privileges")
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class Privilege implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private PrivilegeGroup group; // группа

    @Column(name = "name", length = 32, unique = true, nullable = false)
    private String name; // наименование

    @Column(name = "key", length = 128, unique = true, nullable = false)
    private String key; // ключ

    @Column(name = "description", length = 512)
    private String description; // описание

    @ManyToMany(mappedBy = "privilegeList", cascade = CascadeType.ALL)
    private List<User> userList = new ArrayList<>(); // список пользователей

    @ManyToMany(mappedBy = "privilegeList", cascade = CascadeType.ALL)
    private List<Role> roleList = new ArrayList<>(); // список ролей
}