package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.constant.BaseConstant;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.LAZY;

/**
 * Сущность с описанием таблицы хранения вхождений спецификации изделия
 * @author mazur_ea
 * Date:   29.05.2020
 */
@Entity
@Table(name = "bom_items")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class BomItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "bomItem")
    @org.hibernate.annotations.GenericGenerator(name = "bomItem", strategy = BaseConstant.GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Version
    @Column(name = "lock_version")
    private long lockVersion; // версия блокировки

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bom_id", nullable = false)
    private Bom bom; // версия изделия

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    private Component component; // компонент

    @Column(name = "given_raw_material")
    private boolean givenRawMaterial; // давальческое сырье

    @Column(name = "quantity")
    private double quantity; // количество

    @ManyToMany(cascade = {PERSIST, MERGE})
    @JoinTable(
        name = "bom_item_producers",
        joinColumns = @JoinColumn(name = "bom_item_id"),
        inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    private List<Company> producerList = new ArrayList<>(); // список изготовителей

    @OneToMany(mappedBy = "bomItem", cascade = ALL, orphanRemoval = true)
    private List<BomItemReplacement> bomItemReplacementList = new ArrayList<>();

    @OneToMany(mappedBy = "bomItem", cascade = ALL, orphanRemoval = true)
    private List<BomItemPosition> bomItemPositionList = new ArrayList<>();
}