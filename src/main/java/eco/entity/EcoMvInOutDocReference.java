package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Сущность с описанием таблицы MV_IN_OUT_DOC_REF
 * @author zhestkov_an
 * Date:   20.12.2019
 */
@Entity
@Table(name = "MV_IN_OUT_DOC_REF")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoMvInOutDocReference implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mat_value_id")
    @JsonIgnore
    private EcoMatValue matValue; // мат.ценности

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "in_out_doc_id")
    @JsonIgnore
    private EcoInOutDocument inOutDocument; // идентификатор  документа

    @Column(name = "price")
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allot_id")
    @JsonIgnore
    private EcoAllotment allotment;

    @Column(name = "verified")
    private Long verified; // проверено (проверка на принадлежность матценности накладной на отгрузку ГП)
}