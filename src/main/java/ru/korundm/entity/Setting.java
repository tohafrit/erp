package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * TODO таблица из АСУ prefs
 * Сущность с описанием таблицы settings
 * @author surov_pv
 * Date:   02.03.2018
 */
@Entity
@Table(name = "settings")
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class Setting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // пользователь

    @Column(name = "code", unique = true, nullable = false)
    private String code; // код настройки

    @Column(name = "value")
    private String value; // значение

    @Column(name = "comment")
    private String comment; // комментарий

    // Геттеры и сеттеры для корректного взаимодействия с котлин-классами

    public String getValue() {
        return value;
    }
}