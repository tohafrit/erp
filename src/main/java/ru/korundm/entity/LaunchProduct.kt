package ru.korundm.entity

import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения информации об изделиях в запуске
 *
 * Изделие считается запускаемым, если его значения по contractAmount или rfContractAmount или rfAssembledAmount больше 0
 * или это изделие является частью состава другого запускаемого изделия
 *
 * versionApproveDate поле означает, что версия в поле version была утверждена к запуску в поле launch.
 * Логика в том, что если версия была единожды утверждена к запуску, то позже в интерфейсе изделий утверждение можно снять.
 * При этом снятии состав изделия в запуске остается таким, будто версия утверждена. Это сделано потому, что для правки спецификации изделия необходимо снимать утверждение.
 * Так же при снятом утверждении можно изменить состав изделия, что может негативно сказаться на расчетах изделия в запуске
 */
@Entity
@Table(name = "launch_product")
data class LaunchProduct(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var ver = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product? = null // изделие

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "launch_id", nullable = false)
    var launch: Launch? = null // запуск

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "version_id", nullable = false)
    var version: Bom? = null // версия изделия

    @Column(name = "version_approve_date")
    var versionApproveDate: LocalDate? = null // дата утверждения версии

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