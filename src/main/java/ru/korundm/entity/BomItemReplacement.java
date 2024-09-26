package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.BomItemReplacementStatus;
import ru.korundm.constant.BaseConstant;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Сущность с описанием таблицы хранения замен вхождения в спецификацию изделия
 * @author mazur_ea
 * Date:   29.05.2020
 */
@Entity
@Table(name = "bom_item_replacements")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class BomItemReplacement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "bomItemReplacement")
    @org.hibernate.annotations.GenericGenerator(name = "bomItemReplacement", strategy = BaseConstant.GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Version
    @Column(name = "lock_version")
    private long lockVersion; // версия блокировки

    @Convert(converter = BomItemReplacementStatus.CustomConverter.class)
    @Column(name = "status", nullable = false)
    private BomItemReplacementStatus status; // статус

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_item_id", nullable = false)
    private BomItem bomItem; // вхождение спецификации изделия

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    private Component component; // компонент

    @Column(name = "purchase")
    private boolean purchase; // флаг обозначения компонента к закупке

    @Column(name = "replacement_date")
    private LocalDate replacementDate; // дата замены

    @Column(name = "status_date")
    private LocalDate statusDate; // дата изменения статуса
}