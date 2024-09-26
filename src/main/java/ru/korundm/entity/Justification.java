package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Сущность с описанием таблицы justifications
 * @author pakhunov_an
 * Date:   08.02.2018
 */
@Entity
@Table(name = "justifications")
@ToString
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    discriminatorType = DiscriminatorType.INTEGER,
    name = "type_id",
    columnDefinition = "TINYINT"
)
public abstract class Justification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "name")
    private String name; // название

    @Column(name = "date", nullable = false)
    private LocalDate date; // дата утверждения

    @Column(name = "note")
    private String note; // комментарий

    @OneToMany(mappedBy = "justification", cascade  = CascadeType.ALL, orphanRemoval = true)
    private List<IndexForecast> indexForecastList; // индексы

    @OneToMany(mappedBy = "justification", cascade = CascadeType.ALL)
    private List<ProductTechnicalProcess> productTechnicalProcessList = new ArrayList<>(); // список техпроцессов

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company; // организация-соисполнитель
}