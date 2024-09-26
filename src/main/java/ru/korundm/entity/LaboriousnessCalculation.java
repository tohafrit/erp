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
 * Сущность с описанием таблицы laboriousness_calculations
 * @author pakhunov_an
 * Date:   07.03.2019
 */
@Entity
@Table(name = "laboriousness_calculations")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class LaboriousnessCalculation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "count")
    private Integer count = 1; // количество техпроцессов

    @Column(name = "with_package")
    private Boolean withPackage; // с упаковкой или без

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_technical_process_id", nullable = false)
    private ProductTechnicalProcess productTechnicalProcess; // техпроцесс

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laboriousness_calculation_id")
    private LaboriousnessCalculation parent; // родительский элемент расчета

    @OneToMany(mappedBy = "parent", cascade  = CascadeType.ALL, orphanRemoval = true)
    private List<LaboriousnessCalculation> childList = new ArrayList<>(); // список элеметов расчета

    public String getName() {
        return getProductTechnicalProcess().getFullName();
    }
}