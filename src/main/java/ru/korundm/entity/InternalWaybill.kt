package ru.korundm.entity

import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения внутренних межскладских накладных (МСН)
 */
@Entity
@Table(name = "internal_waybill")
data class InternalWaybill(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var ver = 0L

    @Column(name = "number")
    var number = 0 // номер

    @Column(name = "year")
    var year = 0 // год

    @Column(name = "create_date")
    var createDate: LocalDate = LocalDate.MIN // дата создания

    @Column(name = "accept_date")
    var acceptDate: LocalDate? = null // дата принятия

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "storage_place_id", nullable = false)
    var storagePlace: StoragePlace? = null // склад хранения

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "accept_user_id")
    var acceptUser: User? = null // принявший пользователь

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "give_user_id")
    var giveUser: User? = null // отпустивший пользователь

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @OneToMany(mappedBy = "internalWaybill")
    var matValueList = mutableListOf<MatValue>() // список мат. ценностей
}