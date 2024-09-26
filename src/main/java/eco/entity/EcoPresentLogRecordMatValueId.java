package eco.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Класс составного ключа сущности EcoPresentLogRecordMatValueReference
 * @author berezin_mm
 * Date:   20.05.2020
 */
@Embeddable
@EqualsAndHashCode(of = {"presentLogRecordId", "matValueId"})
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class EcoPresentLogRecordMatValueId implements Serializable {

    @Column(name = "object_id")
    private Long presentLogRecordId;

    @Column(name = "object_ref_id")
    private Long matValueId;
}