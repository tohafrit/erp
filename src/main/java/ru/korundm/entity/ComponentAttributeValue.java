package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы для хранения значений атрибута категории компонента
 * @author mazur_ea
 * Date: 21.03.2019
 */
@Entity
@Table(name = "component_attribute_values")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class ComponentAttributeValue implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    private Component component; // компонент

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private ComponentAttribute attribute; // атрибут

    @Column(name = "bool_value")
    private Boolean boolValue; // boolean значение

    @Column(name = "long_value")
    private Long longValue; // long значение

    @Column(name = "int_value")
    private Integer intValue; // int значение

    @Column(name = "string_value", length = 128)
    private String stringValue; // строковое значение
}