package ru.korundm.entity

import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant
import javax.persistence.*

/**
 * Сущность с описанием таблицы sapsan_product_bom
 * @author zhestkov_an
 * Date:   09.09.2021
 */
@Entity
@Table(name = "sapsan_product_bom")
class SapsanProductBom(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "sapsanProductBom")
    @GenericGenerator(name = "sapsanProductBom", strategy = BaseConstant.GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Column(name = "order_index")
    var orderIndex: Int? = null // для сортировки

    @Column(name = "prime")
    var prime: Boolean = false //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sapsan_product_id", nullable = false)
    var sapsanProduct: SapsanProduct? = null // сапсановское изделие

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id", nullable = false)
    var bom: Bom? = null // версия изделия
}