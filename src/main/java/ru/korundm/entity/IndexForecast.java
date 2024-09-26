package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы index_forecasts
 * @author pakhunov_an
 * Date:   08.02.2019
 */
@Entity
@Table(name = "index_forecasts")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "index_forecast_type_id")
public class IndexForecast implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "index_year", nullable = false)
    private Short year; // год

    @Column(name = "index_value")
    private String indexValue; // значение индекса

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "justification_id", nullable = false)
    private Justification justification; // обоснование
}