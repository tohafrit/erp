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
 * Date:   26.11.2019
 */
@Entity
@Table(name = "tab_module")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuModule implements Serializable {

    @Id
    @Column(name = "ROWID")
    private Long id;

    @Column(name = "cod_mod_1")
    private String code1;

    @Column(name = "cod_mod_2")
    private String code2;

    @Column(name = "nameCD")
    private String nameCD;

    @Column(name = "obozn")
    private String gost;  // Обозначние по КД.

    @Column(name = "oboznTD")
    private String designationTD;  // Обозначние по ТД.

    @Column(name = "jtc")
    private String jtc;       // Журнал ТУ.

    @Column(name = "naz_mod")
    private String name;      // Название.

    @Column(name = "packOblig")
    private Boolean packOblig;     // обязательна ли упаковка

    // Геттеры и сеттеры для корректного взаимодействия с котлин-классами

    public String getCode1() { return code1; }

    public String getCode2() { return code2; }
}