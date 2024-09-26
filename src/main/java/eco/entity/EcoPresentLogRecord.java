package eco.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.ProductAcceptType;
import ru.korundm.constant.BaseConstant;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сущность с описанием таблицы PRESENT_LOG_RECORD
 * @author berezin_mm
 * Date:   15.10.2019
 */
@Entity
@Table(name = "PRESENT_LOG_RECORD")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoPresentLogRecord implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // идентификатор

    @Column(name = "presentation_number")
    private int number; // порядковый номер в течение календарного года

    @NotNull
    @Column(name = "registration_date")
    private LocalDate registrationDate; // дата регистрации

    @Column(name = "transferforwrapping_date")
    private LocalDate wrappingDate; // дата передачи на упаковку

    @Column(name = "readyforshippment_date")
    private LocalDate shipmentDate; // дата готовности к отгрузке

    @Column(name = "note1")
    private String note1; // комментарий на запись ЖРП для ОТК

    @Column(name = "note2")
    private String note2; // комментарий на запись ЖРП для контролёра

    @Column(name = "advancedstudy_date")
    private LocalDate advancedStudyDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_letter_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoProductionShipmentLetter productionShipmentLetter; // письмо на производство

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "io_doc_id")
    @JsonIgnore
    private EcoInOutDocument inOutDocument; // ссылка на приходный документ

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name="contract_item_group_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoLotGroup lotGroup; // позиция договора

    @OneToMany(mappedBy = "presentLogRecord")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonBackReference
    private List<EcoAllotment> allotmentList = new ArrayList<>();

    @Column(name = "accept_type_id")
    private Long acceptType; // тип приёмки изделий (enum ProductAcceptType.java)

    @OneToMany(mappedBy = "presentLogRecord")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoPresentLogRecordMatValueReference> presentLogRecordMatValueReferenceList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    @JsonIgnore
    private EcoDocument document; // ссылка на документ

    public long getAmount() {
        return getAllotmentList().stream().mapToLong(EcoAllotment::getAmount).sum();
    }

    public String getAcceptTypeValue() {
        return getAcceptType() != null ? ProductAcceptType.Companion.getById(getAcceptType()).getCode() : "";
    }

    /**
     * Метод для выборки серийных номеров
     * @return серийные номера через запятую
     */
    public String getSerialNumbers() {
        return getPresentLogRecordMatValueReferenceList().stream().map(presentLogRecordMatValueReference -> presentLogRecordMatValueReference.getMatValue().getSerialNumber()).collect(Collectors.joining(", "));
    }

    public String getInterWarehouseWaybill() {
        String result = "";
        if (inOutDocument != null && inOutDocument.getDocumentNumber() != null && inOutDocument.getOperationDate() != null) {
            result = inOutDocument.getDocumentNumber() + " от " + inOutDocument.getOperationDate().format(DateTimeFormatter.ofPattern(BaseConstant.DATE_PATTERN));
        }
        return result;
    }
}