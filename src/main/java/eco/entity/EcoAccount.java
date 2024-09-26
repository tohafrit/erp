package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы ACCOUNT
 * @author zhestkov_an
 * Date:   09.10.2019
 */
@Entity
@Table(name = "ACCOUNT")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoAccount implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @Column(name = "code_1c")
    private String code1C; // код в базе 1С

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private EcoCompany company; // организация

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private EcoBank bank; // банк

    @Column(name = "account")
    private String account; // расчетный счет

    @Column(name = "note")
    private String note; // комментарий
}