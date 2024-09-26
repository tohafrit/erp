package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы operation_material
 * @author pakhunov_an
 * Date:   07.10.2019
 */
@Entity
@Table(name = "operation_material")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class OperationMaterial implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "name", nullable = false, unique = true)
    private String name; // название

    @ManyToMany(targetEntity = WorkType.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "operation_material_xref_work_type",
        joinColumns = @JoinColumn(name = "operation_material_id"),
        inverseJoinColumns = @JoinColumn(name = "work_type_id")
    )
    private List<WorkType> workTypeList = new ArrayList<>(); // список операций

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
