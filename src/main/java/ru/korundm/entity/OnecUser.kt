package ru.korundm.entity

import org.hibernate.annotations.Immutable
import ru.korundm.integration.onec.Constant.NULL
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "onec_users")
@Immutable
data class OnecUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null, // идентификатор
) {
    @Column(name = "onec_id", nullable = false)
    var onecId: String = NULL // идентификатор из 1С

    @Column(name = "code", nullable = false)
    var code: String = "" // код

    @Column(name = "surname", nullable = false)
    var surname: String = "" // имя

    @Column(name = "name", nullable = false)
    var name: String = "" // имя

    @Column(name = "patronymic", nullable = false)
    var patronymic: String = "" // имя

    @Column(name = "birthday", nullable = false)
    var birthday: LocalDate? = null

    @Column(name = "active", nullable = false)
    var active: Boolean = true
}