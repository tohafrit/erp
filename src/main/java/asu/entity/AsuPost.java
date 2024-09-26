package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы tab_post
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "tab_post")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuPost implements Serializable {

    @Id
    @Column(name = "ROWID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compNameID")
    private AsuComponentName compName;

    @Column(name = "n_contr")
    private String nContr;

    @Column(name = "n_yach_1")
    private Long nYach1;

    @Column(name = "n_yach_2")
    private Long nYach2;

    @Column(name = "n_yach_3")
    private Long nYach3;

    @Column(name = "n_yach_4")
    private Long nYach4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantID")
    private AsuPlant plant;

    @Column(name = "stockID")
    private Long stockId;

    @Column(name = "personID")
    private Long personId;

    @Column(name = "kol_brak")
    private Long kolBrak;

    @Column(name = "timeIn")
    private Long timeIn;

    @Column(name = "nach_kol")
    private Long nachKol;

    @Column(name = "tek_kol")
    private Long tekKol;

    @Column(name = "cena_comp")
    private Double cenaComp;

    @Column(name = "archive")
    private String archive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentID")
    private AsuPost parent;

    @Column(name = "moduleMoveID")
    private Long moduleMoveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isID")
    private AsuInvoiceString invoiceString;

    @Column(name = "prodModuleID")
    private Long prodModuleId;

    @Column(name = "fict")
    private boolean fict;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rootPostID")
    private AsuPost rootPost;

    @Column(name = "lastCompOutTimeonModule")
    private Long lastCompOutTimeonModule;

    @Column(name = "launchID")
    private Long launchId;

    @Column(name = "rawMat")
    private boolean rawMat;
}