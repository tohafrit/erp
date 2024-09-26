package eco.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы ECOPLAN.PRODUCT_LABOUR
 * @author zhestkov_an
 * Date:   01.05.2020
 */
@Entity
@Table(name = "PRODUCT_LABOUR")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoProductLabourReference implements Serializable {

    @EmbeddedId
    private EcoProductLabourId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private EcoProduct product; // изделие

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_id")
    private EcoProductChargesProtocol productChargesProtocol; // история о затратах на изготовление изделия

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labour_id")
    private EcoLabour labour; // вид работы

    @Column(name = "labour_time")
    private Float labourTime; // трудозатраты

    @Column(name = "order_index")
    private Integer orderIndex; // для сортировки

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("labourPriceId")
    @JoinColumn(name = "labour_price_id",  insertable = false, updatable = false)
    private EcoLabourPrice labourPrice; // расценки на работы организаций, установленные протоколами
}