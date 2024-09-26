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

/**
 * Сущность с описанием таблицы ITEM_REF
 * @author zhestkov_an
 * Date:   09.12.2019
 */
@Entity
@Table(name = "ITEM_REF")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoItemReference implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_group_ref_id")
    @JsonIgnore
    private EcoItemGroupReference itemGroupReference; // ссылка на itemGroupReference

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoAllotment allotment; // ссылка на allotment

    @Column(name = "amount")
    private Long amount; //
}
