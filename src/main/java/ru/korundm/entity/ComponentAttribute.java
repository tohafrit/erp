package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.ComponentAttributeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы атрибутов категории компонента
 * @author mazur_ea
 * Date: 19.03.2019
 */
@Entity
@Table(name = "component_attributes")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class ComponentAttribute implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Convert(converter = ComponentAttributeType.CustomConverter.class)
    @Column(name = "type", nullable = false)
    private ComponentAttributeType type; // тип

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ComponentCategory category; // категория

    @Column(name = "name", length = 64, nullable = false)
    private String name; // наименование

    @Column(name = "description", length = 512)
    private String description; // описание

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComponentAttributePreference> componentAttributePreferenceList = new ArrayList<>();

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComponentAttributeValue> componentAttributeValueList = new ArrayList<>();
}