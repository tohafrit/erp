package ru.korundm.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Класс составного ключа сущности EcoProductLabourReference
 * @author zhestkov_an
 * Date:   01.05.2020
 */
@Embeddable
@EqualsAndHashCode(of = {"productId", "labourPriceId"})
@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ProductLabourId implements Serializable {

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "labour_price_id")
    private Long labourPriceId;
}