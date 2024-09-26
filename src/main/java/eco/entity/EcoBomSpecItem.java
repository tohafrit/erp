package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы BOM_SPEC_ITEM
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "BOM_SPEC_ITEM")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoBomSpecItem implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id")
    @JsonManagedReference
    @JsonIgnore
    private EcoBom bom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_product_id")
    @JsonManagedReference
    @JsonIgnore
    private EcoProduct product;

    @Column(name = "sub_product_count")
    private Long subProductCount;

    @Column(name = "sub_product_kind")
    private Long subProductKind;

    @Column(name = "order_index")
    private Long orderIndex;

    @Column(name = "contractor")
    private Long contractor;

    @Column(name = "is_assembly_unit")
    private boolean isAssemblyUnit;

    @Column(name = "assembly_kind")
    private Long assemblyKind;

    public EcoBom getBom() {
        return bom;
    }

    public EcoProduct getProduct() {
        return product;
    }

    public Long getId() {
        return id;
    }
}