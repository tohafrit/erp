package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.constant.BaseConstant;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы хранения позиций вхождения спецификации изделия
 * @author mazur_ea
 * Date:   29.05.2020
 */
@Entity
@Table(name = "bom_item_positions")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class BomItemPosition implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "bomItemPosition")
    @org.hibernate.annotations.GenericGenerator(name = "bomItemPosition", strategy = BaseConstant.GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Version
    @Column(name = "lock_version")
    private long lockVersion; // версия блокировки

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_item_id", nullable = false)
    private BomItem bomItem; // вхождение спецификации изделия

    @Column(name = "designation", length = 10)
    private String designation; // позиционное обозначение

    @Column(name = "firmware", length = 20)
    private String firmware; // прошивка
}