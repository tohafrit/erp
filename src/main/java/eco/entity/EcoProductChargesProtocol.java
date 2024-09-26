package eco.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы PRODUCT_CHARGES_PROTOCOL
 * @author zhestkov_an
 * Date:   29.08.2019
 */
@Entity
@Table(name = "PRODUCT_CHARGES_PROTOCOL")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoProductChargesProtocol implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "document_id")
    private EcoDocument document; // документ

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoProduct product; // изделие

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private EcoCompany company; // компания

    @Column(name = "purchased_component")
    private Float purchasedComponent; // составляющая цены : ПКИ (теперь это сумма 2-х)

    @Column(name = "special_equip_charges")
    private Float specialEquipCharges; // составляющая цены : Спецоборудование

    @Column(name = "other_charges")
    private Float otherCharges; // составляющая цены : Прочие расходы

    @Column(name = "travel_charges")
    private Float travelCharges; // составляющая цены : Коммандировочные

    @Column(name = "partner_charges")
    private Float partnerCharges; // составляющая цены : Контрагентам

    @Column(name = "price")
    private BigDecimal price; // цена

    @Column(name = "locked")
    private Long locked; // зафиксирован

    @Column(name = "consumer_id")
    private Long consumerId; //

    @Column(name = "tmp")
    private Long tmp; //

    @Column(name = "price_note")
    private String priceNote; // комментарий к цене изделия

    @Column(name = "purch_comp_unpack")
    private double purchasedComponentUnpack; // составляющая цены (без упаковки): ПКИ (теперь это сумма 2-х)

    @Column(name = "spec_eq_charges_unpack")
    private double specialEquipComponentUnpack; // составляющая цены (без упаковки): Спецоборудование

    @Column(name = "other_charges_unpack")
    private double otherChargesUnpack; // составляющая цены (без упаковки): Прочие расходы

    @Column(name = "travel_charges_unpack")
    private double travelChargesUnpack; // составляющая цены (без упаковки): Коммандировочные

    @Column(name = "partner_charges_unpack")
    private double partnerChargesUnpack; // составляющая цены (без упаковки): Контрагентам

    @Column(name = "price_unpack")
    private BigDecimal priceUnpack; // цена(без упаковки)

    @Column(name = "contractor_id")
    private Long contractorId; //

    @Column(name = "launch_cost_eq")
    private double launchCostEq; // затраты на подготовку и освоение новых производств, цехов и агрегатов

    @Column(name = "launch_cost_prod")
    private double launchCostProd; // затраты на подготовку и освоение новых видов продукции и новых технологических процессов

    @Column(name = "gear_cost")
    private double gearCost; // затраты на специальную технологическую оснастку

    @Column(name = "nonproductive")
    private double nonproductive; // внепроизводственные затраты

    @Column(name = "version")
    private double version; // версия формы протокола

    @Column(name = "material")
    private double material; // сырье и основные материалы

    @Column(name = "material_unpack")
    private double materialUnpack; //сырье и основные материалы(без упаковки)

    @Column(name = "add_material")
    private double addMaterial; // вспомогательные материалы

    @Column(name = "add_material_unpack")
    private double addMaterialUnpack; //  вспомогательные материалы(без упаковки)

    @Column(name = "half_unit")
    private double halfUnit; // покупные полуфабрикаты

    @Column(name = "half_unit_unpack")
    private double halfUnitUnpack; // покупные полуфабрикаты(без упаковки)

    @Column(name = "remainder")
    private double remainder; // возвратные отходы (вычитаются)

    @Column(name = "remainder_unpack")
    private double remainderUnpack; // возвратные отходы (вычитаются)(без упаковки)

    @Column(name = "transport")
    private double transport; // транспортно-заготовительные расходы

    @Column(name = "transport_unpack")
    private double transportUnpack; // транспортно-заготовительные расходы(без упаковки)

    @Column(name = "fuel")
    private double fuel; // топливо на технологические цели

    @Column(name = "fuel_unpack")
    private double fuelUnpack; // топливо на технологические цели(без упаковки)

    @Column(name = "energy")
    private double energy; // энергия на технологические цели

    @Column(name = "energy_unpack")
    private double energyUnpack; // энергия на технологические цели(без упаковки)

    @Column(name = "package")
    private double pack; // тара (невозвратная) и упаковка

    @Column(name = "package_unpack")
    private double packUnpack; // тара (невозвратная)

    @Column(name = "launch_cost_eq_unpack")
    private double launchCostEqUnpack; //

    @Column(name = "launch_cost_prod_unpack")
    private double launchCostProdUnpack; //

    @Column(name = "gear_cost_unpack")
    private double gearCostUnpack; //

    @Column(name = "nonproductive_unpack")
    private double nonproductiveUnpack; //

    @Column(name = "n_s")
    private double nS; //

    @Column(name = "n_s_unpack")
    private double nSUnpack; //

    @Column(name = "n_pr")
    private double nPr; //

    @Column(name = "n_pr_unpack")
    private double nPrUnpack; //

    @Column(name = "specialcheck_equip_charges")
    private double specialCheckEquipCharges; //

    @Column(name = "speccheck_eq_charges_unpack")
    private double specialCheckEquipChargesUnpack; //

    @Column(name = "specialresearch_equip_charges")
    private double specialResearchEquipCharges; //

    @Column(name = "specresearch_eq_charges_unpack")
    private double specialResearchEquipChargesUnpack; //

    @Column(name = "componentspurchased")
    private double componentsPurchased; // составляющая цены : Покупные Компл Изд

    @Column(name = "comp_purch_unpack")
    private double componentsPurchasedUnpack; // составляющая цены (без упаковки): Покупные КИ

    @Column(name = "componentsown")
    private double componentsOwn; // составляющая цены: Собствен Компл Изд

    @Column(name = "comp_own_unpack")
    private double componentsOwnUnpack; // составляющая цены (без упаковки): Собствен КИ

    @Column(name = "protocol_number")
    private String protocolNumber; // номер протокола

    @Column(name = "protocol_date")
    private LocalDateTime protocolDate; // дата протокола

    @Column(name = "protocol_note")
    private String protocolNote; // комментарий к протоколу

    @OneToMany(mappedBy = "productChargesProtocol")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoProductLabourReference> productLabourReferenceList = new ArrayList<>();
}