package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY;

/**
 * Сущность с описанием таблицы labour_protocols
 * @author zhestkov_an
 * Date:   01.05.2020
 */
@Entity
@Table(name = "labour_protocols")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class LabourProtocol implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "labourProtocol")
    @GenericGenerator(name = "labourProtocol", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @Column(name = "number")
    private String number; // номер

    @Column(name = "date")
    private LocalDate date; // дата

    @Column(name = "note")
    private String note; // комментарий

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "document_id")
    private Document document; // документ

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "company_id")
    private Company company; // организация
}