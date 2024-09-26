package eco.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode(of = {"snapshotParameterId", "childBomId"})
@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class EcoSnapshotCalculationId implements Serializable {

    @Column(name = "snapshot_param_id")
    private Long snapshotParameterId;

    @Column(name = "child_bom_id")
    private Long childBomId;
}