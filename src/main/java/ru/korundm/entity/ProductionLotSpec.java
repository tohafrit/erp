package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY;

/**
 * Сущность с описанием таблицы production_lot_spec
 * @author zhestkov_an
 * Date:   12.04.2020
 */
@Entity
@Table(name = "production_lot_spec")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class ProductionLotSpec implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "productionLotSpec")
    @GenericGenerator(name = "productionLotSpec", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "production_lot_id", nullable = false)
    private ProductionLot productionLot; // специфицируемый производственный лот

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sub_product_id", nullable = false)
    private Product subProduct; // субпродукт

    @Column(name = "sub_product_amount", nullable = false)
    private Long subProductAmount; // количество субпродукта в изделии

    @Column(name = "order_index")
    private Long orderIndex; // для сортировки

    @OneToMany(mappedBy = "productionLotSpec", cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
    @OrderBy("orderIndex")
    private List<ProductionLotSpecProductionLotReference> productionLotSpecProductionLotReferenceList = new ArrayList<>();
}