package eco.entity;

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
 * Сущность с описанием таблицы BOM
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "BOM")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoBom implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // идентификатор

    @Column(name = "major")
    private int major; // версия (первая цифра версии - v х.1 [1])

    @Column(name = "minor")
    private int minor; // изменение (вторая цифра версии - v 1.x [1])

    @Column(name = "modification")
    private int modification; // модификация (третья цифра версии - v 1.1 [х])

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private EcoProduct product; // изделие

    @Column(name = "file_name")
    private String fileName; // путь к файлу

    @Column(name = "fix_date")
    private LocalDate fixDate; // дата фиксации версии

    @Column(name = "fix_user")
    private String fixUser; // кем зафиксировано

    @Column(name = "fix_modification_old")
    private Long fixModificationOld; // предыдущая фиксированная версия

    @Column(name = "app_contractor_mark")
    private Long appContractorMark; // отметка о том, что изделие изготавливается только утвержденным изготовителем. 1 - только утвержденный

    @Column(name = "half_unit_id")
    private Long halfUnit;

    @Column(name = "mplan_subsection")
    private Long mplanSubsection;

    @Column(name = "version")
    private String version; // версия

    @Column(name = "descriptor")
    private int descriptor; // идентификатор версии

    @Column(name = "created")
    private LocalDate created;

    @Column(name = "production_name")
    private String productionName; // название изделия

    @OneToMany(mappedBy = "bom")
    private List<EcoBomAttribute> bomAttributeList = new ArrayList<>();

    @OneToMany(mappedBy = "bom")
    @OrderBy("orderIndex asc")
    private List<EcoBomSpecItem> bomSpecItemList = new ArrayList<>();

    @OneToMany(mappedBy = "bom")
    private List<EcoBomItem> bomItemList = new ArrayList<>();

    @OneToMany(mappedBy = "bom")
    private List<EcoSapsanProductBom> sapsanProductBomList = new ArrayList<>();

    @OneToMany(mappedBy = "bom", cascade = CascadeType.ALL)
    private List<EcoSnapshot> snapshotList = new ArrayList<>();

    public EcoProduct getProduct() {
        return product;
    }

    public Long getId() {
        return id;
    }
}