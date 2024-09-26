package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы invoices
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "INVOICES")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuInvoice implements Serializable {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "dateIn")
    private Long dateIn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplierID")
    private AsuSupplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantID")
    private AsuPlant plant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractID")
    private AsuContract contract;

    @Column(name = "tcr")
    private Long tcr;

    @Column(name = "tlm")
    private Long tlm;

    @Column(name = "userCrID")
    private Long userCrId;

    @Column(name = "userModID")
    private Long userModId;

    @Column(name = "final")
    private boolean last;
}