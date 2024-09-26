package ru.korundm.integration.onec

object Constant {

    /** Базоый URI для обращения к базе данных 1C */
    const val BASE_URI = "http://1c-server.korundm.local/zup-kadry/odata/standard.odata/"

    object Query {
        /** Окончание любого запроса */
        const val FORMAT = "\$format=json;odata=nometadata"
        /** Выбор определенных полей */
        const val SELECT = "\$select="
        /** Разделитель для начала установки параметров */
        const val PARAMS_SEPARATOR = "?"
        /** Для объединения параметров */
        const val PARAMS_UNITE = "&"
        /** Отбор при получении данных */
        const val FILTER = "\$filter="
        /** Ограничение количества возвращаемых записей */
        const val TOP = "\$top="
        /** Убирает из результата запроса указанное количество записей */
        const val SKIP = "\$skip="
        /** Возвращает количество записей в выборке запроса */
        const val COUNT = "\$count="
        /** Добавляет в результат запроса информацию о количестве записей - allpage(=none) */
        const val INLINE_COUNT = "\$inlinecount="
        /** Сортировка результата запроса - <Реквизит1> asc, <Реквизит2> desc */
        const val ORDER_BY = "\$orderby="
        /** Позволяет вместе с результатами основного запроса получать значения связанных сущностей */
        const val EXPAND = "\$expand="
    }

    object Catalog {
        /** Каталог сотрудников */
        const val EMPLOYEE = "Catalog_Сотрудники"
        /** Подразделения организации */
        const val SUBDIVISIONS = "Catalog_ПодразделенияОрганизаций"
        /** Штатное расписание */
        const val STAFFING_SCHEDULE = "Catalog_ШтатноеРасписание"
    }

    /** Остатки отпуска */
    const val HOLIDAYS = "InformationRegister_НачальныеОстаткиОтпусков"
    /** Фактические отпуска */
    const val ACTUAL_VACATION = "AccumulationRegister_ФактическиеОтпуска_RecordType"
    /** Запланированные отпуска */
    const val INFORMATION_VACATION = "InformationRegister_РеестрОтпусков_RecordType"
    /** Табель учета рабочего времени */
    const val WORK_TIME = "Document_ТабельУчетаРабочегоВремени"

    /** Табель на месяц */
    val MONTH_HOUR_LIST = listOf(
        "Часов1", "Часов2", "Часов3", "Часов4", "Часов5", "Часов6", "Часов7", "Часов8", "Часов9", "Часов10",
        "Часов11", "Часов12", "Часов13", "Часов14", "Часов15", "Часов16", "Часов17", "Часов18", "Часов19",
        "Часов20", "Часов21", "Часов22", "Часов23", "Часов24", "Часов25", "Часов26", "Часов27", "Часов28",
        "Часов29", "Часов30", "Часов31"
    )
    /** Идентификатор организации */
    const val KORUNDM_KEY = "67f159ae-950a-11e1-80bc-003048d51367"
    /** Аналог NULL */
    const val NULL = "00000000-0000-0000-0000-000000000000"
}