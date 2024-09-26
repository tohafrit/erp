package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы содержащей инфоромацию о составе изделий (все вложения развернуты)
 * @author pakhunov_an
 * Date:   26.08.2019
 */
@Entity
@Table(name = "PRODUCT_TOTAL_SPEC")
@ToString
@Setter @Getter
public class EcoProductTotalSpec implements Serializable {

    @EmbeddedId
    private EcoProductTotalSpecId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    @JsonIgnore
    @JsonManagedReference
    private EcoProduct product; // изделие-ЭВМ

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subProductId")
    @JoinColumn(name = "sub_product_id", insertable = false, updatable = false)
    @JsonIgnore
    @JsonManagedReference
    private EcoProduct subProduct; // изделие-модуль

    @Column(name = "sub_product_count", nullable = false)
    private Long subProductCount; // количество таких модулей в изделии с учетом всех вложений
}