package eco.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(of = {"snapshotParameterId", "productId"})
public class EcoSnapshotId implements Serializable {

    @Column(name = "snapshot_param_id", nullable = false, updatable = false)
    private Long snapshotParameterId;

    @Column(name = "product_id", nullable = false, updatable = false)
    private Long productId;
}