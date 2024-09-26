package eco.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Класс составного ключа сущности EcoMatValueAllotmentReference
 * @author berezin_mm
 * Date:   25.06.2020
 */
@Embeddable
@EqualsAndHashCode(of = {"allotmentId", "matValueId"})
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class EcoMatValueAllotmentId implements Serializable {

    @Column(name = "object_id")
    private Long allotmentId;

    @Column(name = "object_ref_id")
    private Long matValueId;
}
