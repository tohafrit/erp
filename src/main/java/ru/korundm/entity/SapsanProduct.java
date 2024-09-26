package ru.korundm.entity;

import lombok.*;
import ru.korundm.constant.BaseConstant;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы sapsan_products
 * @author berezin_mm
 * Date:   29.06.2020
 */
@Entity
@Table(name = "sapsan_products")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class SapsanProduct {

    public SapsanProduct(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "sapsanProduct")
    @org.hibernate.annotations.GenericGenerator(name = "sapsanProduct", strategy = BaseConstant.GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "name")
    private String name;

    @Column(name = "prefix")
    private String prefix;

    @OneToMany(mappedBy = "sapsanProduct")
    private List<SapsanProductBom> sapsanProductBomList = new ArrayList<>();

    // Геттеры и сеттеры  для корректного взаимодействия с котлин-классами

    public String getPrefix() {
        return prefix;
    }

    public List<SapsanProductBom> getSapsanProductBomList() {
        return sapsanProductBomList;
    }
}