package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность с описанием таблицы production_defects (ex. proddefects)
 * @author surov_pv
 * Date:   12.04.2018
 */
@Entity
@Table(name = "production_defects")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ProductionDefect implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "code", unique = true, nullable = false)
    private Integer code; // код

    @Column(name = "background_color", nullable = false)
    private String backgroundColor; // цвет фона

    @Column(name = "font_color", nullable = false)
    private String fontColor; // цвет шрифта

    @Column(name = "description", nullable = false)
    private String description; // описание

    @ManyToMany(
            targetEntity = ProductionArea.class,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            mappedBy = "productionDefectList"
    )
    private Set<ProductionArea> productionAreaList = new HashSet<>(); // список участков с дефектом
}