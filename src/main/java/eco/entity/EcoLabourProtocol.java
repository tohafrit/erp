package eco.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Сущность с описанием таблицы LABOUR_PROTOCOL
 * @author mazur_ea
 * Date:   28.11.2019
 */
@Entity
@Table(name = "LABOUR_PROTOCOL")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoLabourProtocol implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "protocol_number")
    private String protocolNumber; // номер

    @Column(name = "protocol_date")
    private LocalDate protocolDate; // дата

    @Column(name = "protocol_note")
    private String protocolNote; // комментарий

    @Column(name = "document_id")
    private Long documentId; // документ

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private EcoCompany company; // организация
}