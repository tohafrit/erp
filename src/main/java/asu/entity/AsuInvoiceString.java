package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы invoicestrings
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "INVOICESTRINGS")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuInvoiceString implements Serializable {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "num")
    private Long num;

    @Column(name = "account")
    private String account;

    @Column(name = "analAccount")
    private String analAccount;

    @Column(name = "name")
    private String name;

    @Column(name = "nn")
    private String nn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "okeiID")
    private AsuOkei okei;

    @Column(name = "coeff")
    private Double coeff;

    @Column(name = "quan")
    private Double quan;

    @Column(name = "price")
    private Double price;

    @Column(name = "launchID")
    private Long launchId;

    @Column(name = "numInvent")
    private String numInvent;

    @Column(name = "numPassport")
    private String numPassport;

    @Column(name = "numFactory")
    private String numFactory;

    @Column(name = "orderRecord")
    private String orderRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoiceID")
    private AsuInvoice invoice;

    @Column(name = "tcr")
    private Long tcr;

    @Column(name = "tlm")
    private Long tlm;

    @Column(name = "userCrID")
    private Long userCrId;

    @Column(name = "userModID")
    private Long userModId;

    @OneToMany(mappedBy = "invoiceString")
    private List<AsuPost> postList = new ArrayList<>();

    @Formula(
        "(select COALESCE(sum(postTekQuan(tab_post.ROWID)),0)\n"
        + " from tab_post\n"
        + " where tab_post.rootPostID in (\n"
        + "  select tab_post.ROWID\n"
        + "  from tab_post\n"
        + "  where tab_post.isID = ID\n"
        + " )\n"
        + ")"
    )
    private double currentQuantity;

    @Formula(
        "(select COALESCE(sum(postZakQuan(tab_post.ROWID)),0)\n"
        + " from tab_post\n"
        + " where tab_post.rootPostID in (\n"
        + "  select tab_post.ROWID\n"
        + "  from tab_post\n"
        + "  where tab_post.isID = ID\n"
        + " )\n"
        + ")"
    )
    private double reserveQuantity;

    @Formula(
        "(select COALESCE(sum(tab_post.kol_brak)/INVOICESTRINGS.coeff,0)\n"
        + " from tab_post inner join INVOICESTRINGS on tab_post.isID = INVOICESTRINGS.ID\n"
        + " where INVOICESTRINGS.ID = ID\n"
        + ")"
    )
    private double wasteQuantity;
}