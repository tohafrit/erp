package ru.korundm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Сущность с описанием таблицы snapshot_parameters (ex. PROC_SNAPSHOT_PARAM)
 * @author pakhunov_an
 * Date:   10.02.2020
 */
@Entity
@Table(name = "snapshot_parameters")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SnapshotParameter implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    @JsonIgnore
    private Purchase purchase; // расчет закупочной ведомости

    @Column(name = "generateOn", nullable = false)
    private LocalDateTime generateOn; // дата генерации

    @Column(name = "type", nullable = false)
    private Long type; // тип слепка (SnapshotType.java)
}