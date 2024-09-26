package ru.korundm.dto.purchase;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter @Getter
public class Snapshot implements Serializable {

    private Long bomId;
    private Long productId;
    private Integer amount;
    private Integer reserve;
    private Integer amountContract;
    private Integer amountUnpaid;
    private Integer unalloted;
    private Integer amountInternal;
}