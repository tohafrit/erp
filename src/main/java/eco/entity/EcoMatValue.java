package eco.entity;

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
 * Сущность с описанием таблицы MAT_VALUE
 * @author zhestkov_an
 * Date:   20.12.2019
 */
@Entity
@Table(name = "MAT_VALUE")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoMatValue implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @Column(name = "kind")
    private Long kind; // вид мат. ценности

    @Column(name = "owner_id")
    private Integer owner; // владелец мат. ценности

    @Column(name = "target")
    private Integer target; // целевое назначение мат. ценности

    @Column(name = "production_date")
    private LocalDateTime productionDate; // дата выпуска

    @Column(name = "name")
    private String name; // наименование (для изделий, не являющихся продуктами КБ)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private EcoProduct product; // ссылка на продукт

    @Column(name = "serial_number")
    private String serialNumber; // серийный номер

    @Column(name = "amount")
    private Long amount; // количество (для ПКИ)

    @Column(name = "price")
    private BigDecimal price; // цена (для непродуктов)

    @Column(name = "note")
    private String note; // комментарий

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private EcoContractSection contractSection; // ссылка на договор

    @Column(name = "inf")
    private String inf; //

    @Column(name = "inf_date")
    private LocalDateTime infDate; //

    @Column(name = "gtd")
    private String gtd; //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peo_letter_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private EcoProductionShipmentLetter productionShipmentLetter; // ссылка на письмо

    @Column(name = "mrp")
    private Integer mrp; //

    @Column(name = "status")
    private Long status; //

    @Column(name = "stage")
    private Long stage; //

    @Column(name = "bom")
    private Long bom; // ссылка на версию изделия (Bill Of Material)

    @Column(name = "advancedstudy_date")
    private LocalDateTime advanceStudyDate; //

    @Column(name = "wrapping_date")
    private LocalDateTime wrappingDate; //

    @Column(name = "performer")
    private Long performer; //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_cell")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private EcoStoreCell storeCell; // складская ячейка

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private EcoStoragePlace storagePlace; // место хранения

    @OneToMany(mappedBy = "matValue")
    @JsonIgnore
    private List<EcoMvInOutDocReference> mvInOutDocReferenceList = new ArrayList<>();

    @OneToMany(mappedBy = "matValue")
    private List<EcoPresentLogRecordMatValueReference> presentLogRecordMatValueReferenceList = new ArrayList<>();
}