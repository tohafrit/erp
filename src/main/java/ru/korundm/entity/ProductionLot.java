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
 * Сущность с описанием таблицы production_lots
 * @author zhestkov_an
 * Date:   12.04.2020
 */
@Entity
@Table(name = "production_lots")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ProductionLot implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "productionLot")
    @GenericGenerator(name = "productionLot", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "launch_product_id", nullable = false)
    private LaunchProduct launchProduct; // элемент плана запуска, который породил производственный лот

    @Column(name = "amount", nullable = false)
    private long amount; // количество

    @Column(name = "monthly_scheduled", nullable = false)
    private int monthlyScheduled;

    @Column(name = "note")
    private String note; // комментарий

    @Column(name = "order_index")
    private Long orderIndex; // для упорядочивания

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bom_id", nullable = false)
    private Bom bom; // версия изделия

    @OneToMany(mappedBy = "productionLot", cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
    @OrderBy("orderIndex")
    private List<ProductionLotSpec> productionLotSpecList = new ArrayList<>();

    @OneToMany(mappedBy = "productionLot", cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
    @OrderBy("orderIndex")
    private List<LaunchProductProductionLotReference> launchProductProductionLotReferenceList = new ArrayList<>();

    @OneToMany(mappedBy = "productionLot", cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
    @OrderBy("orderIndex")
    private List<ProductionLotSpecProductionLotReference> productionLotSpecProductionLotReferenceList = new ArrayList<>();

    /**
     * Метод для получения количества изделий текущего запуска, произведенные в прошлых запусках (верхний уровень изделий)
     * Расчет used_for_sale в V_PL_USED_FOR_SALE
     * @return общее количество изделий
     */
    public Long getAmountPastLaunches() {
        return getLaunchProductProductionLotReferenceList().stream().mapToLong(LaunchProductProductionLotReference::getAmount).sum();
    }

    /**
     * Метод для получения количества составляющих изделия для изделий текущего запуска, произведенных в прошлых запусках (нижний уровень изделий)
     * Расчет used_by_others в V_PL_USED_BY_OTHERS
     * @return общее количество изделий
     */
    public Long getComponentAmountPastLaunches() {
        return getProductionLotSpecProductionLotReferenceList().stream().mapToLong(ProductionLotSpecProductionLotReference::getAmount).sum();
    }

    /**
     * Метод для получения остатка задела текущего запуска
     * Расчет in_reserve в V_LP_IN_RESERVE
     * @return общее количество изделий
     */
    public Long getAmountInReserve() {
        return amount - getAmountPastLaunches() - getComponentAmountPastLaunches();
    }
}