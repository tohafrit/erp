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
 * Сущность с описанием таблицы ECOPLAN.R_PLRECORD_MATVAL
 * @author berezin_mm
 * Date:   20.05.2020
 */
@Entity
@Table(name = "R_PLRECORD_MATVAL")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoPresentLogRecordMatValueReference implements Serializable {

    @EmbeddedId
    private EcoPresentLogRecordMatValueId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("presentLogRecordId")
    @JoinColumn(name = "object_id", insertable = false, updatable = false)
    @JsonIgnore
    @JsonManagedReference
    private EcoPresentLogRecord presentLogRecord; // предъявление

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("matValueId")
    @JoinColumn(name = "object_ref_id",  insertable = false, updatable = false)
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoMatValue matValue; // материальная ценность

    @Column(name = "order_index")
    private Integer orderIndex; // для сортировки
}