package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import ru.korundm.util.KtCommonUtil.numberInYear
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения информации о служебных записках
 */
@Entity
@Table(name = "launch_note")
data class LaunchNote (
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Version
    var version = 0L

    @Column(name = "year")
    var year = 0 // год

    @Column(name = "number")
    var number = 0 // номер

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Column(name = "create_date")
    var createDate: LocalDate? = null // дата создания

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "created_by")
    var createdBy: User? = null // создавший пользователь

    @Column(name = "agreement_date")
    var agreementDate: LocalDate? = null // дата согласования

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "agreed_by")
    var agreedBy: User? = null // согласовавший пользователь

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount

    val numberInYear // номер в году
        get() = numberInYear(year, number)
}