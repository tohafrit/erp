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
 * Сущность с описанием таблицы labour_prices
 * @author zhestkov_an
 * Date:   01.05.2020
 */
@Entity
@Table(name = "labour_prices")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class LabourPrice implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "labour_price")
    @GenericGenerator(name = "labour_price", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "labour_id")
    private Labour labour; // вид работы

    @Column(name = "hourly_pay")
    private Double hourlyPay; // норма оплаты

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "labour_protocol_id")
    private LabourProtocol labourProtocol; // протокол расценки

    @Column(name = "order_index")
    private Long orderIndex; // для сортировки

    @OneToMany(mappedBy = "labourPrice")
    private List<ProductLabourReference> productLabourReferenceList = new ArrayList<>();
}