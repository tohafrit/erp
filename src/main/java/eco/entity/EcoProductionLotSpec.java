package eco.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы cпецификаций производственного лота - фиксируется спецификация изделия на момент запуска
 * @author pakhunov_an
 * Date:   26.08.2019
 */
@Entity
@Table(name = "PRODUCTION_LOT_SPEC")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoProductionLotSpec implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // идентификатор

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_lot_id", nullable = false)
    @JsonIgnore
    @JsonManagedReference
    private EcoProductionLot productionLot; // специфицируемый производственный лот

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_product_id", nullable = false)
    @JsonIgnore
    @JsonManagedReference
    private EcoProduct subProduct; // субпродукт

    @Column(name = "sub_product_amount", nullable = false)
    private Long subProductAmount; // количество субпродукта в изделии

    @Column(name = "order_index")
    private Long orderIndex; // для сортировки

    @OneToMany(mappedBy = "productionLotSpec")
    @JsonIgnore
    @JsonBackReference
    private List<EcoProductionLotSpecProductionLotReference> productionLotSpecProductionLotReferenceList = new ArrayList<>();
}