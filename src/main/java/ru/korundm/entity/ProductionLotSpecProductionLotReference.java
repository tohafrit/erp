package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * Сущность с описанием таблицы production_lot_spec_production_lot_reference
 * @author zhestkov_an
 * Date:   14.04.2020
 */
@Entity
@Table(name = "production_lot_spec_production_lot_reference")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class ProductionLotSpecProductionLotReference implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "order_index")
    private Integer orderIndex; // для упорядочивания

    @Column(name = "amount")
    private Long amount; // количество

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "production_lot_ref")
    private ProductionLot productionLot; // специфицируемый производственный лот

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "production_lot_spec_id")
    private ProductionLotSpec productionLotSpec; // спецификация производственного лота
}