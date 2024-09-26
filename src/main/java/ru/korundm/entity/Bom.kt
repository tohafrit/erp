package ru.korundm.entity

import ru.korundm.util.KtCommonUtil.bomVersion
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения версий изделия
 * @author pakhunov_an
 * Date:   10.02.2020
 */
@Entity
@Table(name = "boms")
data class Bom(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var ver: Long = 0

    @Column(name = "major")
    var major = 0 // версия

    @Column(name = "minor")
    var minor = 0 // изменение

    @Column(name = "modification")
    var modification = 0 // модификация

    @Column(name = "stock")
    var stock = false // флаг задела

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product? = null // изделие

    @Column(name = "production_name")
    var productionName: String? = null // производственное наименование

    @Column(name = "fix_date")
    var fixDate: LocalDate? = null // дата фиксации

    @Column(name = "create_date")
    var createDate: LocalDate? = null // дата создания

    @Column(name = "descriptor", unique = true, nullable = false)
    var descriptor = 0L // идентификатор

    @OneToMany(mappedBy = "bom")
    var bomItemList = mutableListOf<BomItem>()

    @OneToMany(mappedBy = "bom")
    var bomAttributeList = mutableListOf<BomAttribute>()

    @OneToMany(mappedBy = "bom")
    var sapsanProductBomList = mutableListOf<SapsanProductBom>()

    val version
        get() = bomVersion(major, minor, modification)
}