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
 * Сущность с описанием таблицы ECOPLAN.T_STORAGE_PLACES
 * @author berezin_mm
 * Date:   23.01.2020
 */
@Entity
@Table(name = "T_STORAGE_PLACES")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoStoragePlace implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @Column(name = "place")
    private String place; // место

    @OneToMany(mappedBy = "storagePlace")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoMatValue> matValueList = new ArrayList<>(); // материальные ценности
}
