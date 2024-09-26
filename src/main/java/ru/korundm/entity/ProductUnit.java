package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы единиц измерений изделий
 * @author surov_pv
 * Date:   07.02.2019
 */
@Entity
@Table(name = "product_units")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ProductUnit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @Column(name = "unit")
    private String unit; // обозначение единиц

    @Column(name = "desription")
    private String description; // описание единиц
}
