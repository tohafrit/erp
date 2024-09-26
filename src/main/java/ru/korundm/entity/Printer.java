package ru.korundm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы printers
 * @author berezin_mm
 * Date:   24.09.2019
 */
@Entity
@Table(name = "printers")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class Printer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "name", unique = true, nullable = false)
    private String name; // название принтера

    @Column(name = "ip")
    private String ip; // ip-адрес

    @Column(name = "port")
    private Integer port; // порт

    @Column(name = "description")
    private String description; // описание

    @ManyToMany
    @JoinTable(
        name = "users_xref_printers",
        joinColumns = @JoinColumn(name = "printer_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private List<User> userList = new ArrayList<>(); // список принтеров пользователя
}