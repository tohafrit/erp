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
 * Сущность с описанием представления сводной таблицы изделий в служебных записках
 */
@Entity
@Immutable
@Table(name = "v_launch_note_product")
data class LaunchNoteProductView(
    @Id
    @Column(name = "product_id")
    val productId: Long = 0L
) : RowCountable {

    @Column(name = "launch_id")
    val launchId: Long = 0L

    @Column(name = "name")
    val name = ""

    @Column(name = "launch")
    val launch = ""

    @Column(name = "for_contract")
    val forContract = 0

    @Column(name = "rf_contract")
    val rfContract = 0

    @Column(name = "rf_assemble")
    val rfAssemble = 0

    @Column(name = "main_product")
    val mainProduct = 0

    @Column(name = "in_other_product")
    val inOtherProduct = 0

    @Column(name = "to_launch")
    val toLaunch = 0

    @Column(name = "ufrf_contract")
    val ufrfContract = 0

    @Column(name = "ufrf_contract_in_other_product")
    val ufrfContractInOtherProduct = 0

    @Column(name = "ufrf_assemble")
    val ufrfAssemble = 0

    @Column(name = "used_from_reserve")
    val usedFromReserve = 0

    @Column(name = "need")
    val need = 0

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}