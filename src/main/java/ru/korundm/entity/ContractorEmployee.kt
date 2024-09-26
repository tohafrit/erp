package ru.korundm.entity

import ru.korundm.util.KtCommonUtil.userFullName
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы для хранения информации о сотрудниках контрагентов
 */
@Entity
@Table(name = "contractor_employee")
data class ContractorEmployee(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var ver = 0L

    @Column(name = "first_name", length = 32)
    var firstName = "" // имя

    @Column(name = "middle_name", length = 32)
    var middleName: String? = null // отчество

    @Column(name = "last_name", length = 32)
    var lastName = "" // фамилия

    val fullName
        get() = userFullName(lastName, firstName, middleName)
}