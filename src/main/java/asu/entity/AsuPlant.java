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
 * Сущность с описанием таблицы tab_prinadl
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "tab_prinadl")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuPlant implements Serializable {

    @Id
    @Column(name = "ROWID")
    private Long id;

    @Column(name = "cod_prin")
    private Long codPrin;

    @Column(name = "cod_pr")
    private Long codPr;

    @Column(name = "naz_prin")
    private String nazPrin;

    @Column(name = "lettersSign")
    private String lettersSign;
}
