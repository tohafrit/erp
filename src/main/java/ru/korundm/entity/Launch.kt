package ru.korundm.entity

import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import ru.korundm.util.KtCommonUtil.numberInYear
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения запусков
 */
@Entity
@Table(name = "launch")
data class Launch(
    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "launch")
    @GenericGenerator(name = "launch", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Version
    var version = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "launch_id")
    var launch: Launch? = null // null - целевой запуск, иначе дополнительный

    @Column(name = "year")
    var year = 0 // год

    @Column(name = "number")
    var number = 0 // номер

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Column(name = "approval_date")
    var approvalDate: LocalDate? = null // дата утверждения

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "approved_by")
    var approvedBy: User? = null // утвердивший пользователь

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    val numberInYear // номер в году
        get() = numberInYear(year, number, launch?.number)

    @OneToMany(mappedBy = "launch")
    var launchList = mutableListOf<Launch>()

    @OneToMany(mappedBy = "launch")
    var launchProductList = mutableListOf<LaunchProduct>() // запускаемые изделия

    override fun rowCount() = rowCount
}