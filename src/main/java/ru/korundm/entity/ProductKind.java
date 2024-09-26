package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.constant.BaseConstant;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы типов изделий (ex. T_PRODUCT_KIND)
 * @author pakhunov_an
 * Date:   14.04.2019
 */
@Entity
@Table(name = "product_kinds")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ProductKind implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "productKind")
    @org.hibernate.annotations.GenericGenerator(name = "productKind", strategy = BaseConstant.GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "type")
    private String type; // обозначение

    @Column(name = "description")
    private String description; // описание

    @Column(name = "order_index")
    private Long orderIndex; // значение сортировки
}