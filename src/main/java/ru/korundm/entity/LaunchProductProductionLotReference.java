package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Сущность с описанием таблицы launch_product_production_lot_reference
 * @author zhestkov_an
 * Date:   14.04.2020
 */
@Entity
@Table(name = "launch_product_production_lot_reference")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class LaunchProductProductionLotReference implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "order_index")
    private Integer orderIndex; // для упорядочивания

    @Column(name = "amount", nullable = false)
    private Long amount; // количество

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_lot_ref", nullable = false)
    private ProductionLot productionLot; // специфицируемый производственный лот

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launch_product_id", nullable = false)
    private LaunchProduct launchProduct; // элемент плана запуска, который породил производственный лот
}