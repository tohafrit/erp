package ru.korundm.entity

import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant
import ru.korundm.helper.RowCountable
import javax.persistence.*

/**
 * Сущность с описанием таблицы banks
 * @author zhestkov_an
 * Date:   23.03.2021
 */
@Entity
@Table(name = "banks")
data class Bank(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "bank")
    @GenericGenerator(name = "bank", strategy = BaseConstant.GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Column(name = "code_1c")
    var code1C: String? = null // код в базе 1С

    @Column(name = "name", nullable = false)
    var name: String? = null // наименование

    @Column(name = "location")
    var location: String? = null // местонахождения

    @Column(name = "bik")
    var bik: String? = null // бик

    @Column(name = "corr_account")
    var correspondentAccount: String? = null // корр.счет

    @Column(name = "address")
    var address: String? = null // адрес

    @Column(name = "phone")
    var phone: String? = null // телефон

    @OneToMany(mappedBy = "bank")
    var accountList = mutableListOf<Account>() // расчётные счета

    @Formula(BaseConstant.PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}