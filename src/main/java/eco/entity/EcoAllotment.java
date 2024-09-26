package eco.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.PriceKindType;
import ru.korundm.enumeration.ShipmentPermissionKind;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы ALLOTMENT
 * @author zhestkov_an
 * Date:   29.08.2019
 */
@Entity
@Table(name = "ALLOTMENT")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoAllotment implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoLot lot; // партия

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoLaunchProduct launchProduct; // группа изделия

    @Column(name = "amount")
    private Long amount; // количество изделий из группы, относящихся к этой партии

    @Column(name = "price", nullable = false)
    private BigDecimal price; // цена изделия

    @Convert(converter = PriceKindType.CustomConverter.class)
    @Column(name = "price_kind")
    private PriceKindType priceKind; // тип цены (enum PriceKindType.kt)

    @Column(name = "price_protocol")
    private Long priceProtocol; //

    @Column(name = "paid", nullable = false)
    private BigDecimal paid; //

    @Column(name = "shipment_date")
    private LocalDateTime shipmentDate; // дата отгрузки

    @Column(name = "note")
    private String note; // комментарий

    @Column(name = "order_index")
    private Long orderIndex; // для упорядочивания

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_shipment_letter_id")
    @JsonIgnore
    private EcoProductionShipmentLetter productionShipmentLetter; // письмо на отгрузку

    @Column(name = "final_price")
    private BigDecimal finalPrice; //

    @Column(name = "shipment_permit_date")
    private LocalDateTime shipmentPermitDate; // дата разрешения на отгрузку

    @Column(name = "intended_shipment_date")
    private LocalDateTime intendedShipmentDate; //

    @Column(name = "request_id")
    private Long requestId; //

    @Column(name = "transferforwrapping_date")
    private LocalDateTime transferForWrappingDate; // дата передачи на упаковку

    @Column(name = "readyforshippment_date")
    private LocalDateTime readyForShipmentDate; // дата готовности к отгрузке

    @Convert(converter = ShipmentPermissionKind.CustomConverter.class)
    @Column(name = "shipment_permission_kind")
    private ShipmentPermissionKind shipment; // тип разрещения на отгрузку (enum ShipmentPermissionKind.kt)

    @Column(name = "advancedstudy_date")
    private LocalDateTime advancedStudyDate; //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentation_log_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoPresentLogRecord presentLogRecord; // предъявление

    @Transient
    private BigDecimal costWithVAT; // общая стоимость всех запущенных изделий с учетом НДС

    @Transient
    private BigDecimal percentPaid; // оплачено (%)

    @OneToMany(mappedBy = "allotment")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoItemReference> itemReferenceList = new ArrayList<>();

    @OneToMany(mappedBy = "allotment")
    @JsonIgnore
    private List<EcoMvInOutDocReference> mvInOutDocReferenceList = new ArrayList<>();

    /**
     * Метод для получения номера протокола
     * @return номер протокола
     */
    public String getProtocolNumber() {
        return lot.getLotGroup().getProduct().getEcoProductChargesProtocolList().stream()
            .filter(productChargesProtocol -> productChargesProtocol.getId().equals(priceProtocol))
            .findFirst().map(EcoProductChargesProtocol::getProtocolNumber).orElse(null);
    }

    /**
     * Метод для получения даты протокола
     * @return дата протокола
     */
    public LocalDateTime getProtocolDate() {
        return lot.getLotGroup().getProduct().getEcoProductChargesProtocolList().stream()
            .filter(productChargesProtocol -> productChargesProtocol.getId().equals(priceProtocol))
            .findFirst().map(EcoProductChargesProtocol::getProtocolDate).orElse(null);
    }

    /**
     * Метод для получения нужной цены изделия
     * @return цена изделия
     */
    public BigDecimal getNeededPrice() {
        BigDecimal neededPrice = BigDecimal.ZERO;
        // Если у allotment тип цены либо "Предварительный" либо "Экспортный":
        if (priceKind.equals(PriceKindType.PRELIMINARY.getId()) || priceKind.equals(PriceKindType.EXPORT.getId())) {
            neededPrice = price;
            // Если у allotment тип цены "По ведомости" и у lot тип цены "По протоколу":
        } else {
            if (priceKind.equals(PriceKindType.STATEMENT.getId())) {
                PriceKindType.FINAL.getId();
            }
            if (priceKind.equals(PriceKindType.FINAL.getId())) {
                BigDecimal protocolPrice = lot.getLotGroup().getProduct().getEcoProductChargesProtocolList().stream()
                    .filter(productChargesProtocol -> productChargesProtocol.getId().equals(priceProtocol))
                    .findFirst().map(EcoProductChargesProtocol::getPrice).orElse(neededPrice);
                if (!protocolPrice.equals(neededPrice)) {
                    neededPrice = protocolPrice;
                } else {
                    neededPrice = lot.getLotGroup().getProduct().getEcoProductChargesProtocolList().stream()
                        .filter(productChargesProtocol -> productChargesProtocol.getId().equals(priceProtocol))
                        .findFirst().map(EcoProductChargesProtocol::getPriceUnpack).orElse(neededPrice);
                }
            } else {
                neededPrice = lot.getPrice();
            }
        }
        return neededPrice;
    }
}