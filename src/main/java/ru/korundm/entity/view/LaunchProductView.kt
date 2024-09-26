package ru.korundm.entity.view

import org.hibernate.annotations.Formula
import org.springframework.data.annotation.Immutable
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Сущность с описанием представления сводной таблицы изделий в запусках
 */
@Entity
@Immutable
@Table(name = "v_launch_product")
data class LaunchProductView(
    @Id
    @Column(name = "product_id")
    val productId: Long = 0L
) : RowCountable {

    @Column(name = "product_type_id")
    val productTypeId: Long = 0L

    @Column(name = "launch_id")
    val launchId: Long = 0L

    @Column(name = "has_pretender")
    val hasPretender = false

    @Column(name = "is_launched")
    val isLaunched = false

    @Column(name = "product_name")
    val productName = ""

    @Column(name = "ver_id")
    val verId: Long? = null

    @Column(name = "ver_major")
    val verMajor: Int? = null

    @Column(name = "ver_minor")
    val verMinor: Int? = null

    @Column(name = "ver_modification")
    val verModification: Int? = null

    @Column(name = "residue_reserve_contract")
    val residueReserveContract: Int = 0 // остаток заделов предыдущих запусков по договору

    @Column(name = "residue_reserve")
    val residueReserve: Int = 0 // остаток заделов предыдущих запусков

    @Column(name = "in_struct_reserve")
    val inStructReserve: Int = 0 // в составе заделов предыдущих запусков

    @Column(name = "pretenders")
    val pretenders: Int = 0 // Претенденты

    @Column(name = "for_contract")
    val forContract: Int = 0 // по договору

    @Column(name = "reserve_contract")
    val reserveContract: Int = 0 // задел по договору

    @Column(name = "reserve")
    val reserve: Int = 0 // задел

    @Column(name = "total")
    val total: Int = 0 // итого

    @Column(name = "launch_in_struct_other")
    val launchInStructOther: Int = 0 // запускается в составе других изделий

    @Column(name = "total_before_used_reserve")
    val totalBeforeUsedReserve: Int = 0 // итого к запуску (до использования заделов)

    @Column(name = "used_reserve_contract")
    val usedReserveContract: Int = 0 // использовано заделов предыдущих запусков по договору

    @Column(name = "in_struct_used_reserve")
    val inStructUsedReserve: Int = 0 // в составе использованных заделов других изделий предыдущих запусков

    @Column(name = "used_reserve_assemble")
    val usedReserveAssemble: Int = 0 // использовано заделов предыдущих запусков для сборки других изделий

    @Column(name = "total_used_reserve")
    val totalUsedReserve: Int = 0 // итого использовано заделов предыдущих запусков

    @Column(name = "total_after_used_reserve")
    val totalAfterUsedReserve: Int = 0 // итого к запуску (после использования заделов)

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}