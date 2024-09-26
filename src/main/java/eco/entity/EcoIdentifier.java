package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Сущность с описанием таблицы IDS
 * @author zhestkov_an
 * Date:   30.09.2019
 */
@Entity
@Table(name = "IDS")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoIdentifier implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @Column(name = "ids_number")
    private String number; // номер идентификатора

    @Column(name = "contract_date")
    private LocalDateTime contractDate; // дата контракта

    @Column(name = "ids_old")
    private String idsOld; // старый идентификатор

    @Column(name = "customer_root") // головной заказчик
    private String customerRoot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    @JsonIgnore
    private EcoBank bank; // банк

    @Column(name = "account_status")
    private Long accountStatus; // состояние отдельного счета (enum Identifier.java)
}