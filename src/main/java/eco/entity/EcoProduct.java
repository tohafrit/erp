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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы изделий
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "PRODUCT")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoProduct implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // идентификатор

    @Column(name = "product_name")
    private String productName; // наименование

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "product_version")
    private String productVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoProductType productType;

    @Column(name = "product_kind")
    private Long productKind;

    @Column(name = "d_number")
    private String dNumber; // децимальнный номер

    @Column(name = "test_status")
    private Long testStatus;

    @Column(name = "note")
    private String note;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "estimate_price")
    private Double estimatePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_product")
    @JsonIgnore
    private EcoProduct baseProduct;

    @Column(name = "launch_group")
    private Long launchGroup;

    @Column(name = "production_name")
    private String productionName;

    @Column(name = "code_1c")
    private String code1c;

    @Column(name = "unit")
    private Long unit;

    @Column(name = "archive_date")
    private LocalDate archiveDate;

    @Column(name = "family_dnumber")
    private String familyDnumber;

    @Column(name = "periodical_exam_act")
    private String periodicalExamAct;

    @Column(name = "periodical_exam_act_date")
    private LocalDate periodicalExamActDate;

    @Column(name = "suffix")
    private String suffix;

    @Column(name = "ovk_code")
    private String ovkCode;

    @Column(name = "bom_type")
    private Long bomType;

    @Column(name = "bom_manufacturer")
    private Long bomManufacturer;

    @Column(name = "zadelnoye")
    private Long zadelnoye;

    @Column(name = "half_unit_type")
    private Long halfUnitType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constructor_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoUserInfo constructor;

    @Column(name = "export_price")
    private BigDecimal exportPrice;

    @Column(name = "check_condition")
    private String checkCondition;

    @Column(name = "ot_note")
    private String otNote;

    @Column(name = "position")
    private String position;

    @Column(name = "template_path")
    private String templatePath;

    @Column(name = "assembling_time")
    private Long assemblingTime;

    @Column(name = "lot_size")
    private Long lotSize;

    @Column(name = "classgroup_id")
    private Long classGroup;

    @OneToMany(mappedBy = "product")
    @OrderBy("major asc, minor asc, modification asc")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoBom> ecoBomList = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoBomSpecItem> ecoBomSpecItemList = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @OrderBy("protocolDate desc")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoProductChargesProtocol> ecoProductChargesProtocolList = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoLotGroup> ecoLotGroupList = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoLaunchProduct> launchProductList = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoProductTotalSpec> productTotalSpecList = new ArrayList<>();

    @OneToMany(mappedBy = "subProduct")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoProductTotalSpec> subProductTotalSpecList = new ArrayList<>();

    @OneToMany(mappedBy = "subProduct")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoProductionLotSpec> productionLotSpecList = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoMatValue> matValueList = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoSnapshot> snapshotList = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoProductLabourReference> productLabourReferenceList = new ArrayList<>();

    /**
     * Метод для получения списка протоколов
     * @return список протоколов
     */
    @JsonIgnore
    public List<EcoProductChargesProtocol> getProductChargesProtocolList() {
        List<EcoProductChargesProtocol> productChargesProtocolList = new ArrayList<>();
        for (var productChargesProtocol : getEcoProductChargesProtocolList()) {
            if (productChargesProtocol.getProtocolDate() != null &&
                    productChargesProtocol.getProtocolDate().isAfter(LocalDateTime.of(2011, 1, 1, 0, 0))) {
                productChargesProtocolList.add(productChargesProtocol);
            }
        }
        return productChargesProtocolList;
    }

    public List<EcoBomSpecItem> getEcoBomSpecItemList() {
        return ecoBomSpecItemList;
    }

    public Long getId() {
        return id;
    }
}