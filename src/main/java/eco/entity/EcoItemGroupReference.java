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
 * Сущность с описанием таблицы ITEM_GROUP_REF
 * @author zhestkov_an
 * Date:   09.12.2019
 */
@Entity
@Table(name = "ITEM_GROUP_REF")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoItemGroupReference implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_ref_id")
    private EcoContractSectionReference contractSectionReference; // ID ссылки на договор и письмо

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_group_id")
    private EcoLotGroup lotGroup; // ссылка на lotGroup

    @OneToMany(mappedBy = "itemGroupReference")
    @JsonIgnore
    private List<EcoItemReference> itemReferenceList = new ArrayList<>(); // ссылка на allotment
}
