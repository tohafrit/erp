package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы tab_module
 * @author pakhunov_an
 * Date:   03.12.2019
 */
@Entity
@Table(name = "MODULEMOVES")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuModuleMove implements Serializable {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "timePer")
    private Long timePer;

    @Column(name = "arch")
    private boolean arch;

    @Column(name = "fromService")
    private boolean fromService;

    @Column(name = "timeFromService")
    private boolean timeFromService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prodSiteOperationID", nullable = false)
    private AsuOperation prodSiteOperation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prodSiteID", nullable = false)
    private AsuUch prodSite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stockID", nullable = true)
    private AsuSklad stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prodModuleID", nullable = false)
    private AsuProdModule prodModule;

    @Transient
    private Object objBk = null;  // дополнительная информация к движению изделия

    public Object getObjBk() {
        this.objBk = null;

        if (getProdSiteOperation().getCode().equals("967999")
            && getProdSite().getFcode().equals("12")
            //  && (new PersonaDAO()).getSaleList().contains(getProdSitePerson())
            && getStock() != null
            && (getStock().getFcode().equals("38")
            || getStock().getFcode().equals("48")
            || getStock().getFcode().equals("58")
        )
        ) {
            this.objBk = "moduleMove.sale";
        }
//        } else {
//            AsuProdModuleService asuProdModuleService = new AsuProdModuleService();
//            AsuUlist ulist = asuProdModuleService.getUlistOnDate(this);
//            if (ulist != null) {
//                ulist.getProdStation().setSortedUlists();
//                this.objBk = ulist;
//            }
//        }


        return this.objBk;
    }


}
