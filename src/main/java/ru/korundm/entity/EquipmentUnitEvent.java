package ru.korundm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием событий, произошедших с единицей оборудования equipment_unit_events
 * @author mazur_ea
 * Date: 07.02.2019
 */
@Entity
@Table(name = "equipment_unit_events")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EquipmentUnitEvent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_unit_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EquipmentUnit equipmentUnit; // единица оборудования

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_unit_event_type_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EquipmentUnitEventType equipmentUnitEventType; // тип события

    @Size(min = 1, max = 128)
    @Column(name = "name", length = 128, nullable = false)
    private String name; // наименование

    @Column(name = "commentary", nullable = false)
    private String commentary; // комментарий

    @Column(name = "event_on", nullable = false)
    private LocalDateTime eventOn; // дата события

    @OneToMany(mappedBy = "equipmentUnitEvent", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<EquipmentUnitEventMeasure> equipmentUnitEventMeasureList = new ArrayList<>();
}