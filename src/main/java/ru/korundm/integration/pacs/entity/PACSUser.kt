package ru.korundm.integration.pacs.entity

import javax.persistence.*

/**
 * Сущность с описанием таблицы pList
 * @author pakhunov_an
 * Date:   19.03.2021
 */
@Entity
@Table(name = "pList")
data class PACSUser(
    @Id
    @Column(name = "id")
    var id: Long? = null // id
) {
    @Column(name = "Name")
    var name = "" // фамилия

    @Column(name = "FirstName")
    var firstName = "" // имя

    @Column(name = "MidName")
    var midName: String? = null // отчество

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    var passageSet: Set<PACSPassage> = HashSet() // список всех прохождений пользователя
}