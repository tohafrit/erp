package ru.korundm.entity

import javax.persistence.*

@Entity
@Table(name = "labor_protection_instruction")
data class LaborProtectionInstruction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null, // идентификатор
) {

    @Column(name = "name", nullable = false)
    var name = "" // наименование

    @Column(name = "number", nullable = false)
    var number = "" // наименование

    @Column(name = "comment")
    var comment: String? = null // комментарий
}
