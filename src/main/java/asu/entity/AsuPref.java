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
 * Сущность с описанием таблицы PREFS
 * @author pakhunov_an
 * Date:   26.11.2019
 */
@Entity
@Table(name = "PREFS")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuPref implements Serializable {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "property")
    private String property;

    @Column(name = "boolVal")
    private Boolean boolVal;

    @Column(name = "boolComments")
    private String boolComments;

    @Column(name = "stringVal")
    private String stringVal;

    @Column(name = "stringComments")
    private String stringComments;
}