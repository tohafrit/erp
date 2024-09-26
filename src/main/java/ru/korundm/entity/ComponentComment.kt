package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения комментариев к компоненту
 * @author mazur_ea
 * Date:   26.08.2021
 */
@Entity
@Table(name = "component_comment")
data class ComponentComment(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    var component: Component? = null // компонент

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null // автор

    @Column(name = "create_datetime")
    var createDatetime: LocalDateTime = LocalDateTime.MIN // дата и время создания

    @Column(name = "comment", length = 1024)
    var comment = "" // комментарий

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}