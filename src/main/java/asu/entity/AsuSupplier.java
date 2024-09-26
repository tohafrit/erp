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
 * Сущность с описанием таблицы suppliers
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "SUPPLIERS")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuSupplier implements Serializable {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "inn")
    private String inn;

    @Column(name = "kpp")
    private String kpp;
}