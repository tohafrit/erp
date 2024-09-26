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
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы производственный лот
 * @author pakhunov_an
 * Date:   26.08.2019
 */
@Entity
@Table(name = "PRODUCTION_LOT")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoProductionLot implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // идентификатор

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launch_product_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private EcoLaunchProduct launchProduct; // элемент плана запуска, который породил производственный лот

    @Column(name = "amount", nullable = false)
    private Long amount; // количество

    @Column(name = "note")
    private String note; // комментарий

    @Column(name = "order_index")
    private Long orderIndex; // для упорядочивания

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id")
    private EcoBom bom; // версия изделия

    @Column(name = "monthly_scheduled", nullable = false)
    private Long monthlyScheduled; //

    @Column(name = "monplan_note")
    private String monplanNote; //

    @OneToMany(mappedBy = "productionLot")
    @JsonIgnore
    @JsonBackReference
    private List<EcoLaunchProductProductionLotReference> launchProductProductionLotReferenceList = new ArrayList<>();

    @OneToMany(mappedBy = "productionLot")
    @JsonIgnore
    @JsonBackReference
    private List<EcoProductionLotSpecProductionLotReference> productionLotSpecProductionLotReferenceList = new ArrayList<>();

    @OneToMany(mappedBy = "productionLot")
    @JsonIgnore
    @JsonBackReference
    private List<EcoProductionLotSpec> productionLotSpecList = new ArrayList<>();
}