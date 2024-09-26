package asu.entity;

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
 * Сущность с описанием таблицы tab_module
 * @author pakhunov_an
 * Date:   03.12.2019
 */
@Entity
@Table(name = "tab_sotrud")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuPersona implements Serializable {

    @Id
    @Column(name = "ROWID")
    private Long id;

    @Column(name = "secondname")
    private String name;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "surname")
    private String surname;

    @Column(name = "tab_num")
    private String tabNum;
}