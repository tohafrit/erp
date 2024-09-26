package ru.korundm.dto.prod

import ru.korundm.helper.RowCountable
import ru.korundm.util.KtCommonUtil.bomVersion

class LaunchDetailListDto(
    val id: Long? = null, // id изделия
    val lpId: Long? = null, // id изделия в запуске
    val productTypeId: Long? = null, // id типа изделия
    val hasPretender: Boolean? = null, // флаг наличия предтендентов на запись
    val isLaunched: Boolean? = null, // флаг запускаемости изделия
    val productName: String? = null, // наименование изделия
    val verId: Long? = null, // id версии изделия в запуске
    val verMajor: Int? = null, // номер основной версии
    val verMinor: Int? = null, // номер изменения версии
    val verMod: Int? = null, // номер модификации версии
    val residueReserveContract: Int? = null, // остаток заделов предыдущих запусков по договору
    val residueReserve: Int? = null, // остаток заделов предыдущих запусков
    val inStructReserve: Int? = null, // в составе заделов предыдущих запусков
    val pretenders: Int? = null, // претенденты
    val forContract: Int? = null, // по договору
    val reserveContract: Int? = null, // задел по договору
    val reserve: Int? = null, // задел
    val total: Int? = null, // итого
    val launchInStructOther: Int? = null, // запускается в составе других изделий
    val totalBeforeUsedReserve: Int? = null, // итого к запуску (до использования заделов)
    val usedReserveContract: Int? = null, // использовано заделов предыдущих запусков по договору
    val inStructUsedReserve: Int? = null, // в составе использованных заделов других изделий предыдущих запусков
    val usedReserveAssemble: Int? = null, // использовано заделов предыдущих запусков для сборки других изделий
    val totalUsedReserve: Int? = null, // итого использовано заделов предыдущих запусков
    val totalAfterUsedReserve: Int? = null, // итого к запуску (после использования заделов)
    val rowCount: Long = 0 // счетчик строк для пагинации
) : RowCountable {
    val version
        get() = bomVersion(verMajor, verMinor, verMod)
    override fun rowCount() = rowCount
}