package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

// TODO переделать на сущность после возобновления сотрудничества
enum class ContractType(
    val id: Long,
    val property: String,
    val code: String
) : EnumConvertible<Long> {

    PRODUCT_SUPPLY(1, "contractType.productSupply", "ПП"),
    SUPPLY_OF_EXPORTED(2, "contractType.supplyOfExported", "ППЭ"),
    SCIENTIFIC_AND_TECHNICAL(4, "contractType.scientificAndTechnical", "НТП"),
    INTERNAL_APPLICATION(8, "contractType.internalApplication", "ВЗ"),
    OTHER(16, "contractType.other", "Д"),
    DESIGN_DOCUMENTATION(32, "contractType.designDocumentation", "КД"),
    SERVICES(64, "contractType.services", "У"),
    ORDER_WITHOUT_EXECUTION_OF_THE_CONTRACT(128, "contractType.orderWithoutExecutionOfTheContract", "З"),
    TEMPORARY_USE(256, "contractType.temporaryUse", "ВП"),
    OFFICIAL_MEMO(512, "contractType.officialMemo", "СЗ"),
    M(1024, "contractType.m", "ОКР"),
    REPAIR_OTK(2048, "contractType.repairOTK", "Р-ОТК"),
    REPAIR_PZ(4096, "contractType.repairPZ", "Р-ПЗ");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
        fun getByCode(code: String) = values().first { it.code == code }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<ContractType, Long>(ContractType::class.java)
}