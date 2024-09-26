package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import javax.persistence.*

/**
 * Сущность с описанием типов услуг
 * @author zhestkov_an
 * Date:   18.06.2021
 */
@Entity
@Table(name = "service_types")
data class ServiceType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Column(name = "name", length = 128, nullable = false)
    var name: String? = null // наименование

    @Column(name = "prefix", length = 128)
    var prefix: String? = null // префикс изделий ведомости поставки

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}