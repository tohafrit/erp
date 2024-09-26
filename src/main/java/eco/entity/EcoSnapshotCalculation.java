package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы PROC_CALC_SNAPSHOT
 * @author pakhunov_an
 * Date:   22.08.2019
 */
@Entity
@Table(name = "PROC_CALC_SNAPSHOT")
//@Table(name = "snapshot_calculations")
@ToString
@Setter @Getter
public class EcoSnapshotCalculation implements Serializable {

    @EmbeddedId
    private EcoSnapshotCalculationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("snapshotParameterId")
    @JoinColumn(name = "snapshot_param_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoSnapshotParameter snapshotParameter; // параметр слепка

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("childBomId")
    @JoinColumn(name = "child_bom_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoBom childBom; // версия изделия

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_bom_id")
    @JsonIgnore
    private EcoBom parentBom; // версия изделия

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_product_id")
    @JsonIgnore
    private EcoProduct childProduct; // изделие

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id")
    @JsonIgnore
    private EcoLaunch launch; // запуск

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "total_contractor")
    private Integer totalContractor;

    @Column(name = "reserve_amount")
    private Integer reserveAmount;

    @Column(name = "amount_contract")
    private Integer amountContract;

    @Column(name = "amount_unpaid")
    private Integer amountUnpaid;

    @Column(name = "amount_unalloted")
    private Integer amountUnalloted;

    @Column(name = "amount_internal")
    private Integer amountInternal;
}