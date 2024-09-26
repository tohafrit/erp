package ru.korundm.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO таблица из ЭКО product_technical_processes
 * Сущность с описанием таблицы product_technical_processes
 * @author pakhunov_an
 * Date:   04.03.2019
 */
@Entity
@Table(name = "product_technical_processes")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class ProductTechnicalProcess implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "name", nullable = false)
    private String name; // наименование техпроцесса

    @Column(name = "source")
    private String source; // название документа с техпроцессом

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // обоснование

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "justification_id", nullable = false)
    private Justification justification; // обоснование

    @Column(name = "approved")
    private Boolean approved; // утвержден

    @OneToMany(mappedBy = "productTechnicalProcess", cascade = CascadeType.ALL)
    @OrderBy("sort asc")
    private List<Laboriousness> laboriousnessList = new ArrayList<>(); // список трудоемкостей

    @OneToMany(mappedBy = "productTechnicalProcess", cascade = CascadeType.ALL)
    private List<LaboriousnessCalculation> laboriousnessCalculationList = new ArrayList<>(); // список расчетов

    @Transient
    private String fullName; // полное наименование техпроцесса

    /**
     * Метод возвращает названия видов работ через запятую
     * @return названия
     */
    public String getLaboriousnessWorkTypeNames() {
        return getLaboriousnessList().stream().map(l -> l.getWorkType().getName()).collect(Collectors.joining(","));
    }

    /**
     * Метод возвращает значения трудоемкостей через запятую
     * @return значения трудоемкости
     */
    public String getLaboriousnessValues() {
        return getLaboriousnessList().stream().map(Laboriousness::getValue).collect(Collectors.joining(","));
    }

    /**
     * Метод возвращает значения поля упаковки из трудоемкостей
     * @return значения упаковки из трудоемкости
     */
    public String getLaboriousnessWithPackages() {
        return getLaboriousnessList().stream()
                .map(laboriousness -> String.valueOf(laboriousness.getWithPackage())).collect(Collectors.joining(","));
    }

    /**
     * Метод возвращает значения поля номера по ТП из трудоемкостей
     * @return значения номера по ТП из трудоемкости
     */
    public String getLaboriousnessNumbers() {
        return getLaboriousnessList().stream()
                .map(Laboriousness::getNumber).collect(Collectors.joining(","));
    }

    /**
     * Метод возвращает общую трудоемкость для техпроцесса с упаковкой
     * @return общая трудоемкость
     */
    public Double getSumWithoutPackage() {
        return getLaboriousnessList().stream()
                .filter(laboriousness -> !laboriousness.getWithPackage())
                .mapToDouble(laboriousness -> Double.valueOf(laboriousness.getValue())).sum();
    }

    /**
     * Метод возвращает общую трудоемкость для техпроцесса без упаковки
     * @return общая трудоемкость
     */
    public Double getSumWithPackage() {
        return getSumWithoutPackage() + getLaboriousnessList().stream()
                .filter(Laboriousness::getWithPackage)
                .mapToDouble(laboriousness -> Double.valueOf(laboriousness.getValue())).sum();
    }

    /**
     * Метод возвращает true, если условие выполняется хотя бы для одного элемента
     * @return true/false
     */
    public boolean isPackageOperation() {
        return getLaboriousnessList().stream().anyMatch(Laboriousness::getWithPackage);
    }
    /**
     * Метод возвращает необходимое название для jsp страниц
     * @return полное наименование
     */
    public String getFullName() {
        return getName() + " " + getProduct().getConditionalName();
    }
}