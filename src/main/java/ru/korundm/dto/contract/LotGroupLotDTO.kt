package ru.korundm.dto.contract

import java.math.BigDecimal
import java.time.LocalDate

/**
 * Класс для хранения элементов LotGroup и Lot
 * @author zhestkov_an
 * Date:   11.03.2021
 */
class LotGroupLotDTO(
    var groupMain: String? = null, // название главной группы группировки столбцов

    // Элементы LotGroup
    var lotGroupId: Long? = null, // идентификатор
    var productId: Long? = null, // идентификатор изделия
    var productName: String = "", // наименование изделия

    // Элементы Lot
    var lotId: Long? = null, // идентификатор
    var amount: Long? = null, // кол-во экземпляров изделия для позиции ведомости
    var deliveryDate: LocalDate? = null, // дата поставки
    var price: BigDecimal? = null, // цена
    var priceKind: String? = null, // вид цены
    var priceKindId: Long? = null, // идентификатор вида цены
    var protocolId: Long? = null, // идентификатор протокола цены
    var specialTestType: String? = null, // спец. проверка
    var specialTestTypeId: Long? = null, // идентификатор спец. проверки
    var acceptType: String? = null, // тип приемки
    var acceptTypeId: Long? = null, // идентификатор типа приемки
    var totalVAT: BigDecimal? = null, // НДС
    var cost: BigDecimal? = null, // общая стоимость lot-a
    var totalLotGroupCost: Double? = null, // общая стоимость lotGroup-a
    var launchAmount: Long = 0L, // количество запущенных изделий
    var shippedAmount: Long = 0L // количество отгруженных изделий в части поставки
)