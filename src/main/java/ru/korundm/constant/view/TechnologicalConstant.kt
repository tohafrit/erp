package ru.korundm.constant.view

/**
 * Класс для хранения технологических настроек/констант
 * @author pakhunov_an
 * Date:   12.12.2019
 */
object TechnologicalConstant {

    /** Код настройки - идентифитор начальника цеха  */
    const val SETTING_AGREED_ID = "AGREED_ID"

    /** Код настройки - идентифитор проверившего ТП  */
    const val SETTING_CHECKED_ID = "CHECKED_ID"

    /** Код настройки - идентифитор завершившего разработку ТП  */
    const val SETTING_DESIGNED_ID = "DESIGNED_ID"

    /** Код настройки - идентифитор представителя ПЗ  */
    const val SETTING_APPROVED_MILITARY_ID = "APPROVED_MILITARY_ID"

    /** Код настройки - идентифитор главного технолога  */
    const val SETTING_APPROVED_TECHNOLOGICAL_ID = "APPROVED_TECHNOLOGICAL_ID"

    /** Код настройки - идентифитор метролога  */
    const val SETTING_METROLOGIST_ID = "METROLOGIST_ID"

    /** Код настройки - идентифитор нормоконтролера  */
    const val SETTING_NORMOCONTROLLER_ID = "NORMOCONTROLLER_ID"

    /** Кратность номера операции  */
    const val MULTIPLICITY = 5

    /** Первый номер операции  */
    const val START_NUMBER = "005"

    /** Типы оборудования для работы с техпроцессами  */
    val EQUIPMENT_TYPE_LIST = listOf("machine_park", "for_tp")
}