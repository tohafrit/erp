package ru.korundm.entity

import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения информации об изделиях в служебной записке
 */
@Entity
@Table(name = "launch_note_product")
data class LaunchNoteProduct(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var version = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    var note: LaunchNote? = null // служебная записка

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "launch_product_id", nullable = false)
    var launchProduct: LaunchProduct? = null // изделие в запуске

    @Column(name = "contract_amount")
    var contractAmount = 0 // количество по договору

    @Column(name = "rf_contract_amount")
    var rfContractAmount = 0 // количество заделов по договору

    @Column(name = "rf_assembled_amount")
    var rfAssembledAmount = 0 // количество заделов для сборки

    @Column(name = "ufrf_contract_amount")
    var ufrfContractAmount = 0 // количество использованых заделов по договору

    @Column(name = "ufrf_assembled_amount")
    var ufrfAssembledAmount = 0 // количество использованых заделов для сборки

    @Column(name = "ufrf_contract_in_other_product_amount")
    var ufrfContractInOtherProductAmount = 0 // количество в составе использованных заделов
}