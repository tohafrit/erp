package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы laboriousness
 * @author pakhunov_an
 * Date:   04.03.2019
 */
@Entity
@Table(name = "laboriousness")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class Laboriousness implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "sort", nullable = false)
    private Integer sort; // последовательность действий

    @Column(name = "value")
    private String value; // значение трудоемкости

    @Column(name = "number", nullable = false)
    private String number; // номер по техническому процессу (ТП)

    @Column(name = "with_package", nullable = false)
    private Boolean withPackage; // с упаковкой или без

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_type_id", nullable = false)
    private WorkType workType; // вид работы

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_technical_process_id", nullable = false)
    private ProductTechnicalProcess productTechnicalProcess; // техпроцесс
}