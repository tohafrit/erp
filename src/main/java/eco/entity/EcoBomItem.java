package eco.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы BOM_ITEM
 * @author mazur_ea
 * Date:   07.08.2019
 */
@Entity
@Table(name = "BOM_ITEM")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoBomItem implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id")
    private EcoBom bom;

    @Column(name = "quantity")
    private double quantity;

    @Column(name = "oem_supplier")
    private Long oemSupplier;

    @Column(name = "tmp_cells")
    private String tmpCells;

    @Column(name = "tmp_analogs")
    private String tmpAnalogs;

    @Column(name = "contractor_mask")
    private Long contractorMask;

    @Column(name = "multiplicity")
    private Long multiplicity;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "filling")
    private String filling;

    @OneToMany(mappedBy = "bomItem")
    private List<EcoBomItemComponent> ecoBomItemComponentList = new ArrayList<>();
}