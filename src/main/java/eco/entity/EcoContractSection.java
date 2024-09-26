package eco.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.ContractType;
import ru.korundm.enumeration.Performer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Сущность с описанием таблицы CONTRACT_SECTION
 * @author zhestkov_an
 * Date:   29.08.2019
 */
@Entity
@Table(name = "CONTRACT_SECTION")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoContractSection implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoContract contract; // контракт

    @Column(name = "section_number")
    private Long number; // номер секции договра (дополнения)

    @Column(name = "section_date")
    private LocalDate date; // дата создания секции договора (дополнения)

    @Column(name = "note")
    private String note; // комментарий

    @Column(name = "archive_date")
    private LocalDate archiveDate; // дата помещения в архив

    @ManyToMany(mappedBy = "contractSectionList", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<EcoProductionShipmentLetter> productionShipmentLetterList = new ArrayList<>(); // список писем договора

    @Column(name = "approve_date")
    private LocalDate approveDate; // дата утверждения

    @Column(name = "shipment_allowed")
    private Long shipmentAllowed; // принудительное разрешение отгрузки для договора

    @Column(name = "external_name")
    private String externalName;

    @Column(name = "pz_copy_date")
    private LocalDate pzCopyDate; // дата передачи копии договора (дополнения) в ПЗ

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ids_id")
    @JsonIgnore
    private EcoIdentifier identifier; // идентификатор Гос.контракта

    @OneToMany(mappedBy = "contractSection")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoLotGroup> lotGroupList = new ArrayList<>(); // партии секции

    @OneToMany(mappedBy = "contractSection")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoInvoice> invoiceList = new ArrayList<>(); // счета секции

    @OneToMany(mappedBy = "contractSection")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoPayment> paymentList = new ArrayList<>(); // платежи секции

    @OneToMany(mappedBy = "contractSection")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoMatValue> matValueList = new ArrayList<>(); // материальные ценности

    @OneToMany(mappedBy = "contractSection")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoInOutDocument> inOutDocumentList = new ArrayList<>(); // накладные

    @Transient
    private BigDecimal vat; // НДС секции

    @Transient
    private BigDecimal totalWithVAT; // общая стоимость секции с учетом НДС

    /**
     * Метод для получения полного номера контракта и дополнительного соглашения, если оно есть
     * @return полный номер контракта (+ номер доп. соглашения)
     */
    public String getFullNumber() {
        StringBuilder fullNumber = new StringBuilder();
        fullNumber.append(getContract().getContractNumber()).append("/")
            .append(Performer.Companion.getById(getContract().getPerformer()).getPrefix()).append("-")
            .append(ContractType.Companion.getById(getContract().getContractType()).getCode()).append("-");
        getContract().getSectionList().stream()
            .map(EcoContractSection::getDate).min(Comparator.comparing(LocalDate::toEpochDay))
            .ifPresent(localDate -> fullNumber.append(Integer.toString(localDate.getYear()).substring(2)));
        if (getNumber() > 0) {
            fullNumber.append(" Доп.№").append(getNumber());
        }
        return fullNumber.toString();
    }

    /**
     * Метод для получения полного внешнего номера контракта и дополнительного соглашения, если оно есть
     * @return полный внешний номер контракта (+ номер доп. соглашения)
     */
    public String getFullExternalNumber() {
        StringBuilder fullExternalNumber = new StringBuilder();
        if (getContract().getExternalName() != null) {
            fullExternalNumber.append(getContract().getExternalName()).append("/")
                .append(Performer.Companion.getById(getContract().getPerformer()).getPrefix()).append("-")
                .append(ContractType.Companion.getById(getContract().getContractType()).getCode()).append("-");
        } else {
            fullExternalNumber.append(getContract().getContractNumber()).append("/")
                .append(Performer.Companion.getById(getContract().getPerformer()).getPrefix()).append("-")
                .append(ContractType.Companion.getById(getContract().getContractType()).getCode()).append("-");
        }
        getContract().getSectionList().stream()
            .map(EcoContractSection::getDate).min(Comparator.comparing(LocalDate::toEpochDay))
            .ifPresent(localDate -> fullExternalNumber.append(Integer.toString(localDate.getYear()).substring(2)));
        if (getNumber() > 0) {
            fullExternalNumber.append(" Доп.№").append(getNumber());
        }
        return fullExternalNumber.toString();
    }
}