package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.helper.RowCountable;
import ru.korundm.util.CommonUtil;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сущность с описанием таблицы производственных участков production_areas (ex. tab_uchastp)
 * @author pakhunov_an
 * Date:   02.04.2018
 */
@Entity
@Table(name = "production_areas")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ProductionArea {

    /** Длина номера участка */
    private static final int LENGTH_CODE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "name", length = 64, nullable = false, unique = true)
    private String name; // наименование

    @Column(name = "code", nullable = false, unique = true, scale = 3)
    private String code; // код

    @Column(name = "technological", nullable = false)
    private boolean technological; // технологический

    @OneToMany(mappedBy = "productionArea")
    private List<Employee> employeeList = new ArrayList<>(); // список работников на участке

    @ManyToMany(targetEntity = ProductionDefect.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "production_area_xref_production_defect",
        joinColumns = @JoinColumn(name = "production_area_id"),
        inverseJoinColumns = @JoinColumn(name = "production_defect_id")
    )
    private Set<ProductionDefect> productionDefectList = new HashSet<>(); // список дефектов на участке

    /**
     * Метод для получения кода участка с ведущими нулями
     * @return код участка
     */
    public String getFormatCode() {
        return CommonUtil.formatZero(getCode(), LENGTH_CODE);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}