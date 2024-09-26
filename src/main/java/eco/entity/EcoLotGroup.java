package eco.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы LOT_GROUP
 * @author zhestkov_an
 * Date:   29.08.2019
 */
@Entity
@Table(name = "LOT_GROUP")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoLotGroup implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_section_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoContractSection contractSection; // секция договора

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoProduct product ; // изделие

    @Column(name = "order_index")
    private Long orderIndex; // номер позиции в договоре

    @Column(name = "note")
    private String note; // коммнетарий

    @Column(name = "kind")
    private Long kind; // вид работ (enum LotGroupKind.java)

    @Column(name = "bom")
    private Long bom; //

    @OneToMany(mappedBy = "lotGroup")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoLot> lotList = new ArrayList<>(); // атрибуты ведомости

    @Transient
    private Long countProduct; // количество изделий в группе

    @Transient
    private BigDecimal amountProduct; // общая стоимость группы

    @Transient
    private BigDecimal amountProductVAT; // НДС группы

    @Transient
    private Long launch; // запущено по группе

    @Transient
    private Long shipment; // отгруженно по группе

    @Transient
    private BigDecimal totalCostLaunchWithVAT; // общая стоимость всех запущенных изделий

    @OneToMany(mappedBy = "lotGroup", cascade = CascadeType.ALL)
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoPresentLogRecord> presentLogRecordList = new ArrayList<>(); // предъявления
}