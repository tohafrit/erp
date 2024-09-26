package ru.korundm.entity

import ru.korundm.enumeration.MenuItemType
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY
import javax.validation.constraints.Size

/**
 * Сущность с описанием таблицы menu_items
 * @author pakhunov_an
 * Date:   17.03.2021
 */
@Entity
@Table(name = "menu_items")
data class MenuItem(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {
    @Size(min = 1, max = 128)
    @Column(name = "name", length = 128, nullable = false)
    var name: String = "" // наименование

    @Size(min = 1, max = 128)
    @Column(name = "href", length = 128, unique = true)
    var href: String? = null // ссылка

    @Column(name = "sort", nullable = false)
    var sort = 0 // сортировка

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    var parent: MenuItem? = null // родительский пункт меню

    @Column(name = "icon", length = 128)
    var icon: String? = null // иконка пунта меню

    @Convert(converter = MenuItemType.CustomConverter::class)
    @Column(name = "type", nullable = false)
    var type: MenuItemType? = null // тип меню

    @OneToMany(mappedBy = "parent")
    @OrderBy("sort asc, name asc")
    var childList = mutableListOf<MenuItem>() // список дочерних пунктов меню

    @OneToOne(mappedBy = "menuItem", fetch = LAZY)
    var documentation: Documentation? = null // документация
}

@Suppress("unused")
object MenuItemM {
    const val ID = "id"
    const val NAME = "name"
    const val ICON = "icon"
    const val HREF = "href"
    const val PARENT = "parent"
    const val DOCUMENTATION = "documentation"
    const val CHILD_LIST = "childList"
    const val SORT = "sort"
    const val TYPE = "type"
}