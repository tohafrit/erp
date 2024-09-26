package eco.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.PriceKindType;
import ru.korundm.enumeration.ProductAcceptType;
import ru.korundm.enumeration.SpecialTestType;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы LOT
 * @author zhestkov_an
 * Date:   29.08.2019
 */
@Entity
@Table(name = "LOT")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoLot implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoLotGroup lotGroup; // позиция ведомости поставки

    @Column(name = "amount")
    private Long amount; // кол-во экземпляров изделия для позиции ведомости

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate; // дата поставки

    @Column(name = "price")
    private BigDecimal price; // цена экземпляра изделия

    @Convert(converter = PriceKindType.CustomConverter.class)
    @Column(name = "price_kind")
    private PriceKindType priceKind; // тип цены (enum PriceKindType.kt)

    @Column(name = "price_protocol")
    private Long priceProtocol;

    @Convert(converter = ProductAcceptType.CustomConverter.class)
    @Column(name = "accept_type")
    private ProductAcceptType acceptType; // тип приёмки изделий (enum ProductAcceptType.kt)

    @Convert(converter = SpecialTestType.CustomConverter.class)
    @Column(name = "special_test_type")
    private SpecialTestType specialTestType; // тип спецпроверки (enum SpecialTestType.kt)

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "contract_stage_id")
    private Long contractStageID;

    @Column(name = "stage_name")
    private String stageName;

    @OneToMany(mappedBy = "lot")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoAllotment> allotmentList= new ArrayList<>(); // партии

    @Transient
    private BigDecimal totalCost; // общая стоимость без учета НДС

    @Transient
    private BigDecimal vat; // НДС

    @Transient
    private BigDecimal totalCostLaunchWithNds; // общая стоимость всех запущенных изделий

    /**
     * Метод для получения нужной цены изделия
     * @return цена изделия
     */
    public BigDecimal getNeededPrice() {
        BigDecimal neededPrice = BigDecimal.ZERO;
        // Если у lot тип цены либо "Предварительный" либо "Экспортный":
        if (priceKind.equals(PriceKindType.PRELIMINARY.getId()) || priceKind.equals(PriceKindType.EXPORT.getId())) {
            neededPrice = price;
        // Если у lot тип цены "По протоколу":
        } else if (priceKind.equals(PriceKindType.FINAL.getId())) {
             BigDecimal protocolPrice = lotGroup.getProduct().getEcoProductChargesProtocolList().stream()
                    .filter(productChargesProtocol -> productChargesProtocol.getId().equals(priceProtocol))
                    .findFirst().map(EcoProductChargesProtocol::getPrice).orElse(neededPrice);
             if (!protocolPrice.equals(neededPrice)) {
                 neededPrice = protocolPrice;
             } else {
                 neededPrice = lotGroup.getProduct().getEcoProductChargesProtocolList().stream()
                         .filter(productChargesProtocol -> productChargesProtocol.getId().equals(priceProtocol))
                         .findFirst().map(EcoProductChargesProtocol::getPriceUnpack).orElse(neededPrice);
             }
        }
        return neededPrice;
    }

    /**
     * Метод для получения номера протокола
     * @return номер протокола
     */
    public String getProtocolNumber() {
        return lotGroup.getProduct().getEcoProductChargesProtocolList().stream()
                .filter(productChargesProtocol ->  productChargesProtocol.getId().equals(priceProtocol))
                .findFirst().map(EcoProductChargesProtocol::getProtocolNumber).orElse(null);
    }

    /**
     * Метод для получения даты протокола
     * @return дата протокола
     */
    public LocalDateTime getProtocolDate() {
        return lotGroup.getProduct().getEcoProductChargesProtocolList().stream()
                .filter(productChargesProtocol ->  productChargesProtocol.getId().equals(priceProtocol))
                .findFirst().map(EcoProductChargesProtocol::getProtocolDate).orElse(null);
    }

    /**
     * Метод для получения общей стоимости изделий в lot-e без учета НДС
     * @return общая стоимость
     */
    public BigDecimal getLotGroupCost() {
        return getNeededPrice().multiply(BigDecimal.valueOf(amount));
    }

    public String getAcceptTypeCode() {
        return acceptType != null ? acceptType.getCode() : "";
    }

    public String getSpecialTestTypeCode() {
        return specialTestType != null ? specialTestType.getCode() : SpecialTestType.WITHOUT_CHECKS.getCode();
    }
}
