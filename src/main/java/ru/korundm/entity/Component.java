package ru.korundm.entity;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import ru.korundm.enumeration.ComponentLifecycle;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY;

/**
 * Сущность с описанием таблицы хранения компонентов
 * @author mazur_ea
 * Date:   12.03.2019
 */
@Entity
@Table(name = "components")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Component {

    public Component(long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "component")
    @GenericGenerator(name = "component", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Version
    @Column(name = "lock_version")
    private long lockVersion; // версия блокировки

    @Column(name = "name", length = 128, nullable = false)
    private String name; // наименование

    @Column(name = "position")
    private Integer position; // позиция

    @Column(name = "processed")
    private boolean processed; // флаг обработки

    @Column(name = "approved")
    private boolean approved; // флаг утверждения

    @Column(name = "modified_datetime", nullable = false)
    private LocalDateTime modifiedDatetime; // дата изменения

    @Column(name = "type")
    private long type; // TODO тип компонента, пока не импортируем в виде справочника ибо хз нужен ли вообще (1 - компонента, 2 - новая компонента, 3 - заглушка)

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "kind_id")
    private ComponentKind kind; // еще один тип

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "producer_id")
    private Company producer; // производитель

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "okei_id")
    private Okei okei; // единицы измерения

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "purpose_id")
    private ComponentPurpose purpose; // назначение

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "installation_id")
    private ComponentInstallationType installation; // тип установки

    @Column(name = "price")
    private Double price; // ориентировочная цена

    @Column(name = "delivery_time")
    private Integer deliveryTime; // срок поставки с завода, нед.

    @Column(name = "doc_path")
    private String docPath; // путь к документации

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "substitute_component_id")
    @NotFound(action = NotFoundAction.IGNORE) // для импортов
    private Component substituteComponent; // компонент-заместитель

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "purchase_component_id")
    @NotFound(action = NotFoundAction.IGNORE) // для импортов
    private Component purchaseComponent; // компонент замены по закупочной спецификации

    @Column(name = "purchase_component_date")
    private LocalDate purchaseComponentDate; // дата добавления компонента к закупке

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ComponentCategory category; // категория

    @Column(name = "description", length = 1024)
    private String description; // описание

    @OneToMany(mappedBy = "component", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComponentAttributeValue> componentAttributeValueList = new ArrayList<>();

    @OneToMany(mappedBy = "component")
    private List<BomItem> bomItemList = new ArrayList<>();

    @OneToMany(mappedBy = "component")
    private List<BomItemReplacement> bomItemReplacementList = new ArrayList<>();

    public String getFormattedPosition() {
        return position == null ? StringUtils.EMPTY : StringUtils.leftPad(String.valueOf(position), 6, '0');
    }

    public List<ComponentLifecycle> getLifecycle() {
        List<ComponentLifecycle> list = new ArrayList<>();
        if (!this.isApproved() && this.getPosition() == null) {
            list.add(ComponentLifecycle.NEW);
        }
        if (this.isApproved()) list.add(ComponentLifecycle.DESIGN);
        if (this.getPosition() != null) list.add(ComponentLifecycle.INDUSTRIAL);
        return list;
    }
}