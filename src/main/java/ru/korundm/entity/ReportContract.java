package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "report_contracts")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ReportContract implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_section_id")
    private ContractSection contractSection; // секция договора

    @Column(name = "report_use", nullable = false)
    private boolean reportUse = true;

    @Column(name = "product_amount", nullable = false)
    private Long productAmount; // кол-во экземпляров изделий в ведомости

    @Column(name = "target_amount_funding", nullable = false)
    private String targetAmountFunding; // целевой объем финансирования

    @Column(name = "contract_price", nullable = false)
    private String contractPrice; // цена контракта

    @Column(name = "material_target", nullable = false)
    private String materialTarget; // материальные затраты

    @Column(name = "payroll_target", nullable = false)
    private String payrollTarget; // затраты ФОТ

    @Column(name = "other_production_target", nullable = false)
    private String otherProductionTarget; // прочие производственные затраты

    @Column(name = "overhead_target", nullable = false)
    private String overheadTarget; // общепроизводственные затраты

    @Column(name = "general_business_target", nullable = false)
    private String generalBusinessTarget; // общехозяйственные затраты

    @Column(name = "group_shipping_target", nullable = false)
    private String groupShippingTarget; // группа отгрузки продукции по выполненным работам

    @Column(name = "cost_sales_target", nullable = false)
    private String costSalesTarget; // себестоимость продаж

    @Column(name = "profit_target", nullable = false)
    private String profitTarget; // прибыль
}