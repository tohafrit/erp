package ru.korundm.constant

import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.format.DateTimeFormatter

/**
 * Класс для хранения базовых констант
 * @author pakhunov_an
 * Date:   27.03.2018
 */
object BaseConstant {

    object JNDI {
        /** JNDI имя для эко-проекта  */
        const val ECO = "java:/jdbc/eco"

        /** JNDI имя для asu-проекта  */
        const val ASU = "java:/jdbc/asupr"

        /** JNDI имя для СКУД */
        const val PACS = "java:/jdbc/pacs"

        /** JNDI имя для 1С - Управление персоналом */
        const val HR = "java:/jdbc/zuphr"
    }

    /**
     * TODO - временная "приблуда", после переезда удалить
     * Идентификатор текущего предприятия - в программах ECO
     */
    const val ECO_MAIN_PLANT_ID = 822823L

    /** Имя аттрибута модели/сессии для получения информации о пользователе  */
    const val MODEL_SESSION_USER_ATTRIBUTE = "currentUser"

    /** Имя аттрибута в модели spring для хранения глобальной модели данных   */
    const val MODEL_GLOBAL_DATA_ATTRIBUTE_NAME = "modelGlobalData"

    /** Тексты сообщений ошибок  */
    const val RESOURCE_BUNDLE_ERROR_MESSAGE = "i18/error"

    /** Ограничение размера загружаемого файла (в байтах)  */
    const val FILE_SIZE_LIMIT = 2000000000L

    /** IP для принтера Printer21  */
    const val PRINTER21_IP = "192.168.21.21"

    /** Порт для принтеров по умолчанию  */
    const val DEFAULT_PRINTER_PORT = 9100

    /** Ссылка на папку Архив в файловой системе (Z:)  */
    const val ARCHIVE_FILESERVER = "https://inf1.korundm.local/archive/Nov_arhiv/DOCS/DOCS/tehnolog/"

    /** Ссылка на папку OPP в файловой системе (U:)  */
    const val OPP_SHARE_FILESERVER = "https://fileserver.korundm.local/oppshare/"

    /** Ссылка на папку Общее в файловой системе (U:)  */
    const val SHARE_FILESERVER = "https://fileserver.korundm.local/share"

    /** Округление цен - количество знаков после запятой  */
    const val SCALE = 2

    /** Константа симовла "_"  */
    const val UNDERSCORE = "_"

    /** Запрос отключения проверки констрейтов внешних ключей  */
    const val SQL_DISABLE_FOREIGN_KEY_CHECKS = "SET FOREIGN_KEY_CHECKS = 0"

    /** Запрос включения проверки констрейтов внешних ключей  */
    const val SQL_ENABLE_FOREIGN_KEY_CHECKS = "SET FOREIGN_KEY_CHECKS = 1"

    /** Строковая ссылка на класс генератор идентификаторов сущности  */
    const val GENERATOR_STRATEGY = "ru.korundm.helper.SpecificIdentityGenerator"

    /** Паттерн для даты  */
    const val DATE_PATTERN = "dd.MM.yyyy"

    /** Паттерн для времени  */
    const val TIME_PATTERN = "HH:mm"

    /** Паттерн для даты и времени  */
    const val DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss"

    /** Паттерн для месяца  */
    const val MONTH_PATTERN = "MM.yyyy"

    /** Паттерн для проверки на содержание цифр  */
    const val ONLY_DIGITAL_PATTERN = "[\\d]+"

    /** Заголовок ajax-запроса  */
    const val AJAX_REQUEST_HEADER = "x-requested-with"

    /** Путь к шаблонам  */
    val TEMPLATE_PATH = "${System.getProperty("jboss.server.base.dir")}${File.separator}template${File.separator}"

    /** Количество строк на страницу для tabulator */
    const val TABULATOR_PAGE_SIZE = 50

    /** Часть запроса для расчета общего количества строк */
    const val PART_QUERY_COUNT_OVER = "COUNT(*) OVER()"

    /** Алиас для счетчика строк */
    const val ROW_COUNT_ALIAS = "rowCount"

    /** Форматтер для даты и времени */
    val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)

    /** Форматтер для даты */
    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN)

    /** Форматтер месяца */
    val MONTH_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(MONTH_PATTERN)

    /** Ноль типа [Long] */
    const val ZERO_LONG = 0L

    /** Единица типа [Long] */
    const val ONE_LONG = 1L

    /** Ноль типа [Int] */
    const val ZERO_INT = 0

    /** Единица типа [Int] */
    const val ONE_INT = 1

    /** Минус единица типа [Int] */
    const val MINUS_ONE_INT = -1

    /** Константа запятой */
    const val COMMA = ","

    /** Форматтер денежных значений */
    val CURRENCY_FORMATTER: DecimalFormat = NumberFormat.getCurrencyInstance() as DecimalFormat

    init {
        val symbols = CURRENCY_FORMATTER.decimalFormatSymbols
        symbols.currencySymbol = ""
        CURRENCY_FORMATTER.decimalFormatSymbols = symbols
    }
}