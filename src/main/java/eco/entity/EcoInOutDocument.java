package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы IN_OUT_DOC
 * @author zhestkov_an
 * Date:   20.12.2019
 */
@Entity
@Table(name = "IN_OUT_DOC")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoInOutDocument implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @Column(name = "kind")
    private Long kind; // вид документа (enum InOutDocumentKind.java)

    @Column(name = "doc_type")
    private Long documentType; // тип документа(приход/расход)

    @Column(name = "doc_number")
    private String documentNumber; // номер документа

    @Column(name = "operation_date")
    private LocalDateTime operationDate; // дата совершения операции

    @Column(name = "subscript_date")
    private LocalDateTime subscriptDate; // дата подписания документа

    @Column(name = "in_corresp_id")
    private Long inCorresp; // внутренний отправитель/получатель

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "out_corresp_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private EcoCompany outCorresp; // Внешний отправитель/получатель

    @Column(name = "return_date")
    private LocalDateTime returnDate; //

    @Column(name = "vat")
    private Integer vat; // учитывается ли НДС

    @Column(name = "punct_plan")
    private String planItem; // Пункт производственного плана

    @Column(name = "is_returned")
    private Integer isReturned; // отметка о возвращении из временного пользования

    @Column(name = "note")
    private String note; // комментарий

    @Column(name = "to_saps_date")
    private LocalDateTime toSapsDate; //

    @Column(name = "account_id")
    private Long account; //

    @Column(name = "is_shiped")
    private Integer isShiped; //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_section_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private EcoContractSection contractSection; // ссылка на договор

    @Column(name = "target")
    private Long target; // вид целевого назначения изделий

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_id", referencedColumnName = "id")
    @JsonIgnore
    private EcoDocument document; // ссылка на документ

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permitter")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private Eco1CUser permitter; // отпуск разрешил

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private Eco1CUser shipper; // отпуск произвёл

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buchg")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private Eco1CUser bookkeeper; // бухгалтер

    @Column(name = "person")
    private String person;

    @Column(name = "argument")
    private String argument; //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private EcoCompany receiver; //

    @Column(name = "proxy_letter")
    private String proxyLetter; //

    @Column(name = "performer")
    private Long performer; //

    @Column(name = "taken_date")
    private LocalDateTime takenDate; //

    @Column(name = "doc_numb")
    private Long documentNumb; // номер документа для автонумерации

    @Column(name = "suffix")
    private Integer suffix;

    @OneToMany(mappedBy = "inOutDocument")
    @JsonIgnore
    private List<EcoPresentLogRecord> presentLogRecordList = new ArrayList<>(); // список предъявлений

    @OneToMany(mappedBy = "inOutDocument")
    @JsonIgnore
    private List<EcoMvInOutDocReference> mvInOutDocReferenceList = new ArrayList<>();
}
