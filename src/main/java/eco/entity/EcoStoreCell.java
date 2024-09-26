package eco.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы ECOPLAN.STORE_CELL
 * @author berezin_mm
 * Date:   22.01.2020
 */
@Entity
@Table(name = "STORE_CELL")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoStoreCell implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @Column(name = "name")
    private String name; // наименование

    @OneToMany(mappedBy = "storeCell")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoMatValue> matValueList = new ArrayList<>(); // материальные ценности
}
