package ru.korundm.entity

import ru.korundm.enumeration.CompanyTypeEnum
import javax.persistence.*

/**
 * Сущность для описания связей компаний/организаций с типом
 * @author zhestkov_an
 * Date:   04.03.2021
 */
@Entity
@Table(name = "company_types")
data class CompanyType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    var company: Company? = null// компания/организация

    @Convert(converter = CompanyTypeEnum.CustomConverter::class)
    @Column(name = "type", nullable = false)
    var type: CompanyTypeEnum? = null // тип
}