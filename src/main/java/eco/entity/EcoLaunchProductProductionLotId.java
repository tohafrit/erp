package eco.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Класс составного ключа сущности EcoLaunchProductProductionLotReference
 * @author zhestkov_an
 * Date:   27.12.2019
 */
@Embeddable
@EqualsAndHashCode(of = {"productionLotId", "launchProductId"})
@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class EcoLaunchProductProductionLotId implements Serializable {

    @Column(name = "object_ref")
    private Long productionLotId;

    @Column(name = "object_id")
    private Long launchProductId;
}