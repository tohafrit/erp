package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.constant.BaseConstant;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы хранения категорий компонента
 * @author mazur_ea
 * Date: 15.03.2019
 */
@Entity
@Table(name = "component_categories")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class ComponentCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "componentCategory")
    @org.hibernate.annotations.GenericGenerator(name = "componentCategory", strategy = BaseConstant.GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ComponentCategory parent; // категория-родитель

    @Column(name = "name", length = 64, unique = true, nullable = false)
    private String name; // наименование

    @Column(name = "unit")
    private boolean unit; // штучная

    @Column(name = "description", length = 512)
    private String description; // описание

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<ComponentCategory> childList = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ComponentAttribute> componentAttributeList = new ArrayList<>();

    /**
     * Метод получения уровня категории в иерархии категорий
     * @return уровень категории в иерархии категорий
     */
    public int level() {
        int level = 1;
        ComponentCategory parent = getParent();
        while (parent != null) {
            parent = parent.getParent();
            level++;
        }
        return level;
    }
}