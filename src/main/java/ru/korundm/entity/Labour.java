package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY;

/**
 * Сущность с описанием таблицы labour
 * @author zhestkov_an
 * Date:   01.05.2020
 */
@Entity
@Table(name = "labour")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class Labour implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "labour")
    @GenericGenerator(name = "labour", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @Column(name = "name")
    private String name; // вид работы

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "company_id")
    private Company company; // исполнитель работы

    @Column(name = "subtraction")
    private Long subtraction; // вычеты для расчёта Без упаковки. 1 - вычитать

    @OneToMany(mappedBy = "labour")
    private List<ProductLabourReference> productLabourReferenceList = new ArrayList<>();
}