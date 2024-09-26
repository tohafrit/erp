package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы связей R_PL_PL_USAGE
 * @author pakhunov_an
 * Date:   26.08.2019
 */
@Entity
@Table(name = "R_PL_PL_USAGE")
@ToString
@Setter @Getter
public class EcoProductionLotSpecProductionLotReference implements Serializable {

    @EmbeddedId
    private EcoProductionLotSpecProductionLotId id;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "amount")
    private Long amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_ref", insertable = false, updatable = false)
    @JsonIgnore
    @JsonManagedReference
    private EcoProductionLot productionLot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id", insertable = false, updatable = false)
    @JsonIgnore
    @JsonManagedReference
    private EcoProductionLotSpec productionLotSpec;
}