package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.FetchType.LAZY;

/**
 * Сущность с описанием таблицы product_labour_reference
 * @author zhestkov_an
 * Date:   01.05.2020
 */
@Entity
@Table(name = "product_labour_reference")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ProductLabourReference implements Serializable {

    @EmbeddedId
    private ProductLabourId id;

    @ManyToOne(fetch = LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product; // изделие

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_charges_protocol_id")
    private ProductChargesProtocol productChargesProtocol; // история о затратах на изготовление изделия

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "labour_id")
    private Labour labour; // вид работы

    @Column(name = "labour_time")
    private Float labourTime; // трудозатраты

    @Column(name = "order_index")
    private Integer orderIndex; // для сортировки

    @ManyToOne(fetch = LAZY)
    @MapsId("labourPriceId")
    @JoinColumn(name = "labour_price_id")
    private LabourPrice labourPrice; // расценки на работы организаций, установленные протоколами
}