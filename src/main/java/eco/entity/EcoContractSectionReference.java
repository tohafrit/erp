package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы CONTRACT_SECTION_REF
 * @author zhestkov_an
 * Date:   09.12.2019
 */
@Entity
@Table(name = "CONTRACT_SECTION_REF")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoContractSectionReference implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_shipment_letter_id")
    private EcoProductionShipmentLetter productShipmentLetter; // ссылка на письмо

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_section_id")
    private EcoContractSection contractSection; // ссылка на договор

    @OneToMany(mappedBy = "contractSectionReference")
    @JsonIgnore
    private List<EcoItemGroupReference> itemGroupReferenceList = new ArrayList<>(); // ссылка на lot group и allotment
}
