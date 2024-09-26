package ru.korundm.constant.view

import ru.korundm.enumeration.CompanyTypeEnum
import ru.korundm.enumeration.ContractType

/**
 * Класс для хранения настроек/констант для договоров
 * @author pakhunov_an
 * Date:   24.12.2019
 */
object ContractConstant {

    /** Типы договоров при поиске  */
    var CONTRACT_TYPE_SEARCH_TYPE_LIST = listOf(
        ContractType.PRODUCT_SUPPLY,
        ContractType.SUPPLY_OF_EXPORTED,
        ContractType.SCIENTIFIC_AND_TECHNICAL,
        ContractType.INTERNAL_APPLICATION,
        ContractType.OTHER,
        ContractType.SERVICES,
        ContractType.ORDER_WITHOUT_EXECUTION_OF_THE_CONTRACT,
        ContractType.REPAIR_OTK,
        ContractType.REPAIR_PZ
    )

    /** Типы внешних договоров  */
    var EXTERNAL_TYPE_LIST = listOf(
        ContractType.PRODUCT_SUPPLY,
        ContractType.SUPPLY_OF_EXPORTED,
        ContractType.SCIENTIFIC_AND_TECHNICAL,
        ContractType.DESIGN_DOCUMENTATION,
        ContractType.SERVICES,
        ContractType.ORDER_WITHOUT_EXECUTION_OF_THE_CONTRACT,
        ContractType.REPAIR_OTK,
        ContractType.REPAIR_PZ,
        ContractType.OTHER
    )

    /** Внутренний договор  */
    var INTERNAL_TYPE = ContractType.INTERNAL_APPLICATION

    /** Список внутренних организаций  */
    var INNER_CUSTOMERS = listOf(
        CompanyTypeEnum.KORUND_M.id,
        CompanyTypeEnum.NIISI.id,
        CompanyTypeEnum.SAPSAN.id,
        CompanyTypeEnum.OAO_KORUND_M.id
    )

    const val COMPLEX = 4L // Тип изделия : Комплекс
    const val NAVIGATION_EQUIPMENT = 7L // Тип изделия : Навигационная аппаратура
    const val SERVICE = 8L // Тип изделия : Услуги
    const val DESIGN_DOCUMENTATION = 9L // Тип изделия : Конструкторская документация
    const val CHIP = 11L // Тип изделия : Микросхема
    const val BLANK = 12L // Тип изделия : Заготовка

    /** Список типов изделий, которые не учавствуют в добавлении к договору  */
    var EXCLUDE_PRODUCT_TYPE_LIST = listOf(
        COMPLEX,
        NAVIGATION_EQUIPMENT,
        SERVICE,
        DESIGN_DOCUMENTATION,
        CHIP,
        BLANK
    )

    /** Процентная величина аванса по умолчанию  */
    const val DEFAULT_ADVANCE = 70

    /** Список типов услуг, у которых отсутствует префикс  */
    const val MANUFACTURING = 1L // Изготовление продукции
    const val EXPORT_MANUFACTURING = 2L // Изготовление продукции в экспортном исполнении
    const val SCIENTIFIC_AND_TECHNICAL_MANUFACTURING = 3L // Изготовление научно-технической продукции
    const val INTERNAL_MANUFACTURING = 4L // Изготовление продукции по внутренним заявкам
    const val ORDER_MANUFACTURING = 5L // Изготовление продукции по заказам
}