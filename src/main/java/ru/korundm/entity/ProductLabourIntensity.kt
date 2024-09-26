package ru.korundm.entity

import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения трудоемкостей изготовления и проверки изделий
 */
@Entity
@Table(name = "product_labour_intensity")
data class ProductLabourIntensity(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var version = 0L

    @Column(name = "name", length = 128)
    var name: String = "" // наименование

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Column(name = "create_date")
    var createDate: LocalDate = LocalDate.MIN // дата создания

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: User? = null // кем создан

    @OneToMany(mappedBy = "labourIntensity")
    var entryList = mutableListOf<ProductLabourIntensityEntry>()
}