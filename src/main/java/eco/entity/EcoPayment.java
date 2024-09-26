package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность с описанием таблицы PAYMENT
 * @author zhestkov_an
 * Date:   18.10.2019
 */
@Entity
@Table(name = "PAYMENT")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoPayment implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private EcoInvoice invoice; // счет

    @Column(name = "payment_number")
    private String number; // номер платежа

    @Column(name = "payment_date")
    private LocalDateTime date; // дата платежа

    @Column(name = "amount")
    private BigDecimal amount; // сколько денег

    @Column(name = "note")
    private String note; // комментарий

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private EcoAccount account; // расчетный счет

    @Column(name = "code_1c")
    private String code1C; // код в базе 1С

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_section_id")
    @JsonIgnore
    @JsonManagedReference
    private EcoContractSection contractSection; // секция контракта

    @Column(name = "advance_invoice")
    private String advanceInvoice; //
}