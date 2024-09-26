package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы ECOPLAN.BOM_COMPONENT
 * @author mazur_ea
 * Date:   07.08.2019
 */
@Entity
@Table(name = "BOM_COMPONENT")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoBomComponent implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoBomComponentCategory category;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "is_processed")
    private Boolean isProcessed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_component_proxy")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoBomComponent componentProxy;

    @Column(name = "is_strategic")
    private Boolean isStrategic;

    @Column(name = "is_final")
    private Boolean isFinal;

    @Column(name = "reserve_current")
    private Long reserveCurrent;

    @Column(name = "cell")
    private String cell;

    @Column(name = "supplier_id")
    private Long supplier;

    @Column(name = "doc_path")
    private String docPath;

    @Column(name = "type")
    private Long type;

    @Column(name = "price")
    private Double price;

    @Column(name = "pack_norm")
    private Long packNorm;

    @Column(name = "delivery_terms")
    private Long deliveryTerms;

    @Column(name = "last_modified")
    private String lastModified;

    @Column(name = "kind_id")
    private Long kind;

    @Column(name = "width")
    private Long width;

    @Column(name = "height")
    private Long height;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_component_id")
    @JsonIgnore
    private EcoBomComponent purchaseComponent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit")
    @JsonIgnore
    private EcoUnit unit;

    @Column(name = "purchase_comp_date")
    private LocalDate purchaseCompDate;

    @Column(name = "installation_type")
    private Long installationType;

    @Column(name = "consumable")
    private Long consumable;

    @OneToMany(mappedBy = "component")
    @JsonIgnore
    private List<EcoBomItemComponent> ecoBomItemComponentList = new ArrayList<>();
}