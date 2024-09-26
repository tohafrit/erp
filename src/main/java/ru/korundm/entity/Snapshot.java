package ru.korundm.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import eco.entity.EcoBom;
import eco.entity.EcoSnapshotId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы snapshots (ex. PROC_SNAPSHOT)
 * @author pakhunov_an
 * Date:   10.02.2020
 */
@Entity
@Table(name = "snapshots")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class Snapshot implements Serializable {

    @EmbeddedId
    private SnapshotId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("snapshotParameterId")
    @JoinColumn(name = "snapshot_parameter_id", insertable = false, updatable = false)
    private SnapshotParameter snapshotParameter; // параметр слепка

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id", insertable = false, updatable = false)
    private Bom bom; // версия изделия

    @Column(name = "amount")
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // изделие

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launch_id", nullable = false)
    private Launch launch;
}