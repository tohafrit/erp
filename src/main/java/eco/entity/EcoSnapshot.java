package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.stream.Collectors;


/**
 * Сущность с описанием таблицы PROC_SNAPSHOT
 * @author pakhunov_an
 * Date:   22.08.2019
 */
@Entity
@Table(name = "PROC_SNAPSHOT")
//@Table(name = "snapshots")
@ToString
@Setter @Getter
public class EcoSnapshot implements Serializable {

    @EmbeddedId
    private EcoSnapshotId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("snapshotParameterId")
    @JoinColumn(name = "snapshot_param_id", insertable = false, updatable = false)
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoSnapshotParameter snapshotParameter; // параметр слепка

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id", insertable = false, updatable = false)
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoBom bom; // версия изделия

    @Column(name = "amount")
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoProduct product; // изделие

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

    @org.hibernate.annotations.Formula("amount - reserve_amount")
    private Integer count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_attribute_id", nullable = false)
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoLaunch launch;

    @Transient
    private Boolean accepted;

    @Transient
    private EcoBomAttribute approved;

    /**
     * Метод для получения списка префиксов
     * @return список префиксов в виде строки
     */
    public String getSapsanProductPrefix() {
        if (getBom() != null) {
            return getBom().getSapsanProductBomList().stream()
                    .map(spb -> spb.getSapsanProduct().getPrefix()).collect(Collectors.joining(", "));
        }
        return null;
    }
}