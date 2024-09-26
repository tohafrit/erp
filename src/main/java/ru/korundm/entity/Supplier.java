package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы suppliers
 * @author surov_pv
 * Date:   05.04.2018
 */
@Entity
@Table(name = "suppliers")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class Supplier implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @Size(min = 1, max = 128)
    @Column(name = "name", length = 128, nullable = false)
    private String name; // наименование

    @org.hibernate.validator.constraints.Range(max = 9999999999999L)
    @Column(name = "inn")
    private Long inn; // ИНН

    @org.hibernate.validator.constraints.Range(max = 999999999L)
    @Column(name = "kpp")
    private Long kpp; // КПП
}
