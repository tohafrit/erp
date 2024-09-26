package eco.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы PROC_SNAPSHOT_PARAM
 * @author pakhunov_an
 * Date:   22.08.2019
 */
@Entity
@Table(name = "PROC_SNAPSHOT_PARAM")
//@Table(name = "snapshot_parameters")
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoSnapshotParameter implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // идентификатор

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proc_param_id", nullable = false)
    @JsonIgnore
    private EcoPurchase purchase; // расчет закупочной ведомости

    @Column(name = "date_generate", nullable = false)
    private LocalDateTime generateOn; // дата генерации

    @Column(name = "type_id", nullable = false)
    private Long type; // тип слепка (SnapshotType.java)

    @OneToMany(mappedBy = "snapshotParameter", cascade = CascadeType.ALL)
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoSnapshot> snapshotList = new ArrayList<>();
}