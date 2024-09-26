package eco.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.Performer;
import ru.korundm.enumeration.ShipmentLetterKind;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сущность с описанием таблицы PRODUCTION_SHIPMENT_LETTER
 * @author zhestkov_an
 * Date:   29.08.2019
 */
@Entity
@Table(name = "PRODUCTION_SHIPMENT_LETTER")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoProductionShipmentLetter implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // идентификатор

    @Column(name = "creation_date")
    private LocalDateTime creationDate; // дата создания письма

    @Column(name = "shipment_date")
    private LocalDateTime shipmentDate; // дата отгрузки

    @Column(name = "creator")
    private String creator; // исполнитель

    @Column(name = "letter_number")
    private Integer letterNumber; // номер письма - порядковый номер письма в рамках года

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_ref", referencedColumnName = "id")
    @JsonIgnore
    private EcoDocument document; // документ

    @Column(name = "note")
    private String note;

    @Column(name = "let_type")
    private Long type;

    @Column(name = "performer")
    private Long performer; // организация-исполнитель (enum Performer.java)

    @Column(name = "kind")
    private Long kind; // тип письма (enum ShipmentLetterKind.java)

    @Column(name = "sent_date")
    private LocalDateTime dateSent;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "CONTRACT_SECTION_REF",
        joinColumns = @JoinColumn(name = "prod_shipment_letter_id"),
        inverseJoinColumns = @JoinColumn(name = "contract_section_id")
    )
    @JsonIgnore
    private List<EcoContractSection> contractSectionList = new ArrayList<>(); // список контрактов письма

    @OneToMany(mappedBy = "productionShipmentLetter")
    @JsonIgnore
    private List<EcoAllotment> allotmentList = new ArrayList<>();

    @OneToMany(mappedBy = "productionShipmentLetter")
    @JsonBackReference
    private List<EcoMatValue> matValueList = new ArrayList<>();

    @OneToMany(mappedBy = "productShipmentLetter")
    @JsonIgnore
    private List<EcoContractSectionReference> contractSectionReferenceList = new ArrayList<>();

    @OneToMany(mappedBy = "productionShipmentLetter", cascade = CascadeType.ALL)
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoPresentLogRecord> presentLogRecordList = new ArrayList<>(); // предъявления

    public String getAllFullContractNumbersAndAdditionNumbers() {
        return contractSectionList.stream().map(EcoContractSection::getFullExternalNumber).collect(Collectors.joining("; "));
    }

    public String getFullNumber() {
        StringBuilder sb = new StringBuilder();
        Performer performer = Performer.Companion.getById(getPerformer());
        switch (ShipmentLetterKind.getById(getKind())) {
            case OFFICIAL_MEMO:
                sb.append("СЛ-");
                if (performer == Performer.KORUND || performer == Performer.OAOKORUND) {
                    sb.append(performer.getPrefix()).append("-");
                }
                break;
            case LETTER:
                if (performer == Performer.NIISI || performer == Performer.KORUND) {
                    sb.append(performer.getPrefix()).append("-");
                }
                break;
            case OFFICIAL_MEMO_OKR:
                if (performer == Performer.NIISI || performer == Performer.KORUND) {
                    sb.append("ОКР-").append(performer.getPrefix()).append("-");
                }
                break;
        }
        return sb.append(getLetterNumber()).toString();
    }
}