package ru.korundm.entity

import org.hibernate.annotations.Immutable
import ru.korundm.integration.onec.Constant.NULL
import javax.persistence.*

@Entity
@Table(name = "subdivisions")
@Immutable
data class Subdivision(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null, // идентификатор
) {
    @Column(name = "onec_id", nullable = false)
    var onecId: String = NULL // идентификатор из 1С

    @Column(name = "name", nullable = false)
    var name: String = "" // наименование подразделения

    @Column(name = "sort", nullable = false)
    var sort = ""

    @Column(name = "formed", nullable = false)
    var formed = false

    @Column(name = "disbanded", nullable = false)
    var disbanded = false

    @Column(name = "parent_id", nullable = false)
    var parentId: String = "" // подразделение-родитель
}