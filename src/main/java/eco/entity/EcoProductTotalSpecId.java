package eco.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(of = {"productId", "subProductId"})
public class EcoProductTotalSpecId implements Serializable {

    @Column(name = "product_id", nullable = false, updatable = false)
    private Long productId;

    @Column(name = "sub_product_id", nullable = false, updatable = false)
    private Long subProductId;
}