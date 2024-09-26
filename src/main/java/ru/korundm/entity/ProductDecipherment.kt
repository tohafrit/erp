package ru.korundm.entity

import ru.korundm.helper.FileStorable
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения расшифровок цены изделия
 * @author mazur_ea
 * Date:   07.07.2021
 */
@Entity
@Table(name = "product_decipherment")
data class ProductDecipherment(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : FileStorable<ProductDecipherment> {

    @Version
    var version = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    var period: ProductDeciphermentPeriod? = null // период

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    var type: ProductDeciphermentType? = null // тип расшифровки

    @Column(name = "ready")
    var ready = false // готовность

    @Column(name = "approved")
    var approved = false // утверждено

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Column(name = "create_date")
    var createDate: LocalDate = LocalDate.MIN // дата создания

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: User? = null // кем создано

    override fun storableId() = id

    override fun storableClass() = ProductDecipherment::class
}