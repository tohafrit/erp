package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы 1c_users
 * @author berezin_mm
 * Date:   13.04.2020
 */
@Entity
@Table(name = "1c_users")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class User1C implements Serializable {

    @Id
    @Column(name = "user_id")
    private Long id; // id

    @Column(name = "user_descr")
    private String name; // имя пользователя
}