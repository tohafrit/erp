package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.InvoiceStatus;
import ru.korundm.enumeration.InvoiceType;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность с описанием таблицы INVOICE
 * @author zhestkov_an
 * Date:   08.10.2019
 */
@Entity
@Table(name = "INVOICE")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoInvoice implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_section_id")
    @JsonIgnore
    @JsonManagedReference
    private EcoContractSection contractSection; // секция договора

    @Column(name = "invoice_number")
    private Long invoiceNumber; // номер счета

    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate; // дата создания счета

    @Column(name = "amount")
    private BigDecimal amount; // деньги

    @Column(name = "note")
    private String note; // комментарий

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private EcoDocument document; // документ

    @Convert(converter = InvoiceStatus.CustomConverter.class)
    @Column(name = "status")
    private InvoiceStatus invoiceStatus; // статус счета (enum InvoiceStatus.kt)

    @Convert(converter = InvoiceType.CustomConverter.class)
    @Column(name = "type")
    private InvoiceType invoiceType; // тип счета (enum InvoiceType.kt)

    @Column(name = "good_through_date")
    private LocalDateTime goodThroughDate; // счет действителен до

    @Column(name = "paid_amount")
    private BigDecimal paidAmount; // сумма, на которую оплачен счет
}