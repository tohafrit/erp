package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы R_LP_PL_USAGE
 * @author zhestkov_an
 * Date:   27.12.2019
 */
@Entity
@Table(name = "R_LP_PL_USAGE")
@ToString
@Setter @Getter
public class EcoLaunchProductProductionLotReference implements Serializable {

    @EmbeddedId
    private EcoLaunchProductProductionLotId id;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "amount")
    private Long amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productionLotId")
    @JoinColumn(name = "object_ref", insertable = false, updatable = false)
    @JsonIgnore
    @JsonManagedReference
    private EcoProductionLot productionLot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id",  insertable = false, updatable = false)
    @MapsId("launchProductId")
    @JsonIgnore
    private EcoLaunchProduct launchProduct;
}