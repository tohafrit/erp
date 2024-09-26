package eco.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы связей версий изделий и сапсановких изделий
 * @author pakhunov_an
 * Date:   23.08.2019
 */
@Entity
@Table(name = "R_SAPSAN_PRODUCT_BOM")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoSapsanProductBom implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "prime")
    private boolean prime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_ref_id")
    private EcoSapsanProduct sapsanProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id")
    private EcoBom bom;
}