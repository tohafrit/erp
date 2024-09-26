package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.ComponentAttributePreferenceType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы хранения настроек атрибута категории компонента
 * @author mazur_ea
 * Date: 21.03.2019
 */
@Entity
@Table(name = "component_attribute_preferences")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class ComponentAttributePreference implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Convert(converter = ComponentAttributePreferenceType.CustomConverter.class)
    @Column(name = "type", nullable = false)
    private ComponentAttributePreferenceType type; // тип

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private ComponentAttribute attribute; // атрибут

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preference_id")
    private ComponentAttributePreference preference; // ссылка на настройку

    @Column(name = "sort_index")
    private int sortIndex; // индекс сортировки

    @Column(name = "bool_value")
    private Boolean boolValue; // boolean значение

    @Column(name = "long_value")
    private Long longValue; // long значение

    @Column(name = "int_value")
    private Integer intValue; // int значение

    @Column(name = "string_value", length = 128)
    private String stringValue; // строковое значение

    @OneToMany(mappedBy = "preference", cascade = CascadeType.ALL)
    private List<ComponentAttributePreference> componentAttributePreferenceList = new ArrayList<>();
}