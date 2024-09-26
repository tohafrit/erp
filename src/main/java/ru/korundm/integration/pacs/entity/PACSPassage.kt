package ru.korundm.integration.pacs.entity

import java.time.LocalDateTime
import javax.persistence.*

/**
 * Сущность с описанием таблицы pLogData
 * @author pakhunov_an
 * Date:   20.03.2021
 */
@Entity
@Table(name = "pLogData")
data class PACSPassage(
    @Id
    @Column(name = "id")
    var id: Long? = null // id
) {
    @Column(name = "TimeVal", nullable = false)
    var time: LocalDateTime? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HozOrgan")
    var user: PACSUser? = null
}