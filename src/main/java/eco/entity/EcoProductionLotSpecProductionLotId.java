package eco.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Класс составного ключа сущности EcoProductionLotSpecProductionLotReference
 * @author zhestkov_an
 * Date:   27.12.2019
 */
@Embeddable
@EqualsAndHashCode(of = {"productionLotId", "productionLotSpecId"})
@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class EcoProductionLotSpecProductionLotId implements Serializable {

    @Column(name = "object_ref")
    private Long productionLotId;

    @Column(name = "object_id")
    private Long productionLotSpecId;
}