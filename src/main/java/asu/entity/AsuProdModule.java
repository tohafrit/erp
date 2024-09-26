package asu.entity;

import asu.dao.AsuModuleMoveService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.util.CommonUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сущность с описанием таблицы PRODMODULES
 * @author pakhunov_an
 * Date:   25.11.2019
 */
@Entity
@Table(name = "PRODMODULES")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuProdModule implements Serializable {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "num")
    private int num;

    @Column(name = "tcr")
    private long tcr;

    @Column(name = "comments")
    private String comments;

    @Column(name = "modType")
    private String modType;

    @Column(name = "productionTime")
    private Long productionTime;

    @Column(name = "factoryNumber")
    private String factoryNumber;

    @Column(name = "timeMade")
    private Long timeMade;

    @Column(name = "printBarCode")
    private Boolean printBarCode;

    @Column(name = "particularTime")
    private Long particularTime;

    @Column(name = "warrantyTime")
    private Long warrantyTime;

    @Column(name = "si")
    private Boolean si;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moduleID", nullable = false)
    private AsuModule module; // модуль

    @Column(name = "isRealBarcode")
    private boolean isRealBarcode = true; // реальный выданный штрихкод - true. или доработка - false

    @OneToMany(mappedBy = "prodModule")
    private List<AsuProdModuleInUlist> asuProdModuleInUlistList = new ArrayList<>();

    @Transient
    private Boolean status;

    @org.hibernate.annotations.Formula( "( select MODULEMOVES.ID"
        + " from MODULEMOVES"
        + " where MODULEMOVES.prodModuleID = ID"
        + " and MODULEMOVES.timePer = (select max(MM.timePer)"
        + " from MODULEMOVES MM"
        + " where MM.prodModuleID = MODULEMOVES.prodModuleID"
        + " )"
        + " )"
    )
    private  Long lastModuleMoveID;   // идентификатор последнего движения модуля

    @OneToMany(mappedBy = "prodModule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<AsuModuleMove> moduleMoveSet = new HashSet<>(); // список всех движений продМодуля

    public AsuModuleMove getLastModuleMove(){
      AsuModuleMove moduleMove = null;
      if (lastModuleMoveID != null){
          AsuModuleMoveService service = new AsuModuleMoveService();
          moduleMove = service.read(lastModuleMoveID);
      }
      return moduleMove;
    }

    /**
     * Метод получения серийного номера
     * @return серийный номер {@link String}
     */
    public String getFullBarCode() {
        return
            CommonUtil.formatZero(getModule().getCode1(), 6) +
            CommonUtil.formatZero(getModule().getCode2(), 2) +
            CommonUtil.formatZero(String.valueOf(getNum()), 4);
    }

    // Геттеры и сеттеры для корректного взаимодействия с котлин-классами

    public Long getId() { return id; }

    public AsuModule getModule() { return module; }

    public List<AsuProdModuleInUlist> getAsuProdModuleInUlistList() { return asuProdModuleInUlistList; }
}