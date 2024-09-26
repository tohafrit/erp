package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY;

/**
 * Сущность с описанием таблицы хранения состава спецификации изделия
 * @author mazur_ea
 * Date:   18.05.2020
 */
@Entity
@Table(name = "bom_spec_items")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class BomSpecItem {

    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "bomSpecItem")
    @GenericGenerator(name = "bomSpecItem", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Version
    @Column(name = "lock_version")
    private long lockVersion; // версия блокировки

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bom_id", nullable = false)
    private Bom bom; // версия изделия

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // изделия

    @Column(name = "quantity")
    private int quantity; // количество изделий

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "producer_id")
    private Company producer; // изготовитель

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}