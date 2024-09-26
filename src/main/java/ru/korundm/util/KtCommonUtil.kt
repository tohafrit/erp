package ru.korundm.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.ss.formula.functions.T
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import ru.korundm.constant.BaseConstant.AJAX_REQUEST_HEADER
import ru.korundm.constant.BaseConstant.CURRENCY_FORMATTER
import ru.korundm.constant.BaseConstant.MODEL_SESSION_USER_ATTRIBUTE
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.BaseConstant.ZERO_INT
import ru.korundm.constant.BaseConstant.ZERO_LONG
import ru.korundm.entity.Product
import ru.korundm.entity.ProductChargesProtocol
import ru.korundm.entity.User
import ru.korundm.enumeration.ContractType
import ru.korundm.enumeration.Performer
import ru.korundm.helper.AliasedObjectResultTransformer
import ru.korundm.helper.AttachmentMediaType
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.RowCountable
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.mail.internet.MimeUtility
import javax.persistence.Column
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.primaryConstructor

object KtCommonUtil {

    /**
     * Метод для получения строки номера в году
     * @param year год
     * @param number номер
     * @param parentNumber номер родителя (года при такой логике одинаковые)
     * @return строка номера в году
     */
    fun numberInYear(year: Int, number: Int, parentNumber: Int? = null): String {
        val strYear = year.toString()
        return if (strYear.length > 2 && number > 0) {
            "${parentNumber?.let { "${it}." } ?: ""}$number/${strYear.substring(2)}"
        } else ""
    }

    /**
     * Метод для получения версии изделия
     * @param major major-версия
     * @param minor minor-версия
     * @param mod модификация
     * @return строка версии изделия
     */
    fun bomVersion(major: Int?, minor: Int?, mod: Int?) = major?.let { vmajor -> "v $vmajor${minor?.let { ".$it" } ?: ""}${mod?.let { if (it == 0) "" else " [$it]" } ?: ""}" } ?: ""

    fun contractFullNumber(cNum: Int?, performer: Long?, type: Long?, year: Int?, sNum: Int? = null) =
        "${cNum ?: ""}/${performer?.let { Performer.getById(it).prefix } ?: ""}-${type?.let { ContractType.getById(it).code } ?: ""}-${year?.toString()?.substring(2) ?: ""}${if (sNum != null && sNum > 0) " Доп.№$sNum" else ""}"

    /**
     * Метод для получения списка протоколов
     * @return список протоколов
     */
    fun getChargesProtocolList(product: Product?): List<ProductChargesProtocol> {
        if (product == null) return emptyList()
        val resList = mutableListOf<ProductChargesProtocol>()
        for (pcp in product.productChargesProtocolList) {
            val date = pcp.protocolDate
            if (date != null && date.isAfter(LocalDateTime.of(2011, 1, 1, 0, 0))) {
                resList.add(pcp)
            }
        }
        resList.sortWith(Comparator.comparing(ProductChargesProtocol::protocolDate))
        return resList
    }

    /**
     * Метод проверки http-запроса [HttpServletRequest] на принадлежность к AJAX вызову
     * @return если AJAX-запрос, то true, иначе - false
     */
    fun HttpServletRequest.isAjax() = this.getHeader(AJAX_REQUEST_HEADER) != null

    /**
     * Безопасный метод десереализации JSON-строки в объект класса
     * @param value JSON-строка
     * @param cl класс объекта десереализации
     * @return десереализованный объект
     */
    fun <T : Any> ObjectMapper.safetyReadValue(value: String, cl: KClass<T>): T = try {
        this.readValue(value, cl.java)
    } catch (e: Exception) { cl.primaryConstructor?.call() ?: throw IllegalCallerException("primary constructor required") }

    /**
     * Безопасный метод десереализации JSON-строки в список определенного типа
     * @param value JSON-строка
     * @return десереализованный список объектов
     */
    fun <T : Any> ObjectMapper.safetyReadListValue(value: String, cl: KClass<T>): List<T> = try {
        this.readValue(value, this.typeFactory.constructCollectionType(List::class.java, cl.java))
    } catch (e: Exception) {
        emptyList()
    }

    /**
     * Безопасный метод десереализации JSON-строки в словарь
     * @param value JSON-строка
     * @param keyClass класс ключа в словаре
     * @param valueClass класс значения в словаре
     * @return десереализованный словарь
     */
    fun <K : Any, V : Any> ObjectMapper.safetyReadMapValue(value: String, keyClass: KClass<K>, valueClass: KClass<V>): Map<K, V> = try {
        this.readValue(value, this.typeFactory.constructMapType(HashMap::class.java, keyClass.java, valueClass.java))
    } catch (e: Exception) {
        emptyMap()
    }

    /**
     * Безопасный метод десереализации JSON-строки в изменяемый список определенного типа
     * @param value JSON-строка
     * @return десереализованный изменяемый список объектов
     */
    fun <T : Any> ObjectMapper.safetyReadMutableListValue(value: String, cl: KClass<T>): MutableList<T> = try {
        this.readValue(value, this.typeFactory.constructCollectionType(List::class.java, cl.java))
    } catch (e: Exception) {
        mutableListOf()
    }

    /**
     * Метод получения пользователя [User] из сессии [HttpSession]
     * @return пользователь [User]
     */
    fun HttpSession.getUser() = this.getAttribute(MODEL_SESSION_USER_ATTRIBUTE) as User

    /**
     * Свойство получения названия колонки для классового поля, помеченного аннотацией [Column]
     * @return название колонки, в случае невозможности получения - пустая строка
     */
    val <T, V> KMutableProperty1<T, V>.columnName: String
        get() = (this.annotations.find { it is Column } as? Column)?.name ?: ""

    /**
     * Метод фарматирования строки, если она не пуста
     * @return строка
     */
    inline fun String.ifNotBlank(value: (str: String) -> String?) = if (this.isBlank()) this else value(this)

    /**
     * Метод получения динамического объекта из строки
     * @return динамический объект [DynamicObject]
     */
    fun ObjectMapper.readDynamic(attr: String): DynamicObject = this.readValue(attr, DynamicObject::class.java)

    /**
     * Метод счетчика из списка [RowCountable]
     * @return значение счетчика строк
     */
    fun <T : RowCountable> List<T>.rowCount() = this.getOrNull(ZERO_INT)?.rowCount() ?: ZERO_LONG

    /**
     * Метод преобразования дробного значения в строку валютного формата
     * @return строка валютного формата
     */
    fun Double?.currencyFormat() = this?.let { CURRENCY_FORMATTER.format(it).trim() } ?: ""

    /**
     * Метод-расширение для преобразования результата нативного запроса в объект заданного класса
     * setResultTransformer является устаревшим, поскольку разработчик ORM не предложил альтернативу методу.
     * В 6.0+ версии Hibernate должны добавить поддержку нового способа преобразования
     * @param cl класс объекта преобразования
     * @return [org.hibernate.query.Query]
     */
    fun <T : Any, R : Any> org.hibernate.query.Query<R>.resultTransform(cl: KClass<T>): org.hibernate.query.Query<R> = this.setResultTransformer(AliasedObjectResultTransformer(cl))

    /**
     * Метод-расширение для извлечения списка из БД на замену [javax.persistence.Query.getResultList]
     * @return список сущностей
     */
    @Suppress("unchecked_cast")
    fun <T> javax.persistence.Query.typedManyResult() = this.resultList as List<T>

    /**
     * Метод-расширение для извлечения единственного значения из БД. В отличие [javax.persistence.Query.getSingleResult] не вызывает исключения, а возвращает null
     * @return сущность или null
     */
    fun <T> javax.persistence.Query.typedSingleResult() = this.typedManyResult<T>().getOrNull(0)

    /**
     * Метод отправки .doc файла в http ответ
     * @param doc файл формата .doc
     * @param name название файла
     */
    fun HttpServletResponse.attachDocumentDOC(doc: HWPFDocument, name: String) = attachDocument(doc.toByteArray(), name, AttachmentMediaType.DOC)

    /**
     * Метод отправки .docx файла в http ответ
     * @param doc файл формата .docx
     * @param name название файла
     */
    fun HttpServletResponse.attachDocumentDOCX(doc: XWPFDocument, name: String) = attachDocument(doc.toByteArray(), name, AttachmentMediaType.DOCX)

    /**
     * Метод отправки .xls файла в http ответ
     * @param doc файл формата .xls
     * @param name название файла
     */
    fun HttpServletResponse.attachDocumentXLS(doc: Workbook, name: String) = attachDocument(doc.toByteArray(), name, AttachmentMediaType.XLS)

    /**
     * Метод отправки .xlsx файла в http ответ
     * @param doc файл формата .xlsx
     * @param name название файла
     */
    fun HttpServletResponse.attachDocumentXLSX(doc: Workbook, name: String) = attachDocument(doc.toByteArray(), name, AttachmentMediaType.XLSX)

    /**
     * Метод отправки .zip файла в http ответ
     * @param bytes байт-массив файла
     * @param name название файла
     */
    fun HttpServletResponse.attachDocumentZIP(bytes: ByteArray, name: String) = attachDocument(bytes, name, AttachmentMediaType.ZIP)

    /**
     * Метод преобразования документа в поток байтов
     */
    fun Workbook.toByteArray(): ByteArray {
        val baos = ByteArrayOutputStream()
        this.write(baos)
        baos.close()
        this.close()
        return baos.toByteArray()
    }

    /**
     * Метод преобразования документа в поток байтов
     */
    fun HWPFDocument.toByteArray(): ByteArray {
        val baos = ByteArrayOutputStream()
        this.write(baos)
        baos.close()
        this.close()
        return baos.toByteArray()
    }

    /**
     * Метод преобразования документа в поток байтов
     */
    fun XWPFDocument.toByteArray(): ByteArray {
        val baos = ByteArrayOutputStream()
        this.write(baos)
        baos.close()
        this.close()
        return baos.toByteArray()
    }

    /**
     * Метод отправки байтового потока в http ответ в качестве прикрепляемого документа
     * @param bytes поток байтов
     * @param name название файла
     * @param attachmentType тип прикрепляемого документа
     */
    fun HttpServletResponse.attachDocument(bytes: ByteArray, name: String, attachmentType: AttachmentMediaType) {
        val encodeName = StandardCharsets.UTF_8.displayName()
        this.contentType = attachmentType.contentType
        this.setHeader(CONTENT_DISPOSITION, "attachment; filename=\"${MimeUtility.encodeText(name + "." + attachmentType.extension, encodeName, "Q")}\"")
        this.characterEncoding = encodeName
        this.outputStream.write(bytes)
    }

    /**
     * Метод формирования имени пользователя в формате Фамилия Имя Отчество
     * @param lastName фамилия
     * @param firstName имя
     * @param middleName отчество
     * @param short флаг укорачивания имени и отчества до инициалов
     * @return строку в формате Фамилия И.О. или Фамилия Имя Отчество
     */
    @JvmOverloads
    fun userFullName(lastName: String?, firstName: String?, middleName: String?, short: Boolean = true): String {
        val ln = (lastName ?: "").trim()
        val fn = (firstName ?: "").trim()
        val mn = (middleName ?: "").trim()
        return "$ln ${if (fn.isNotBlank()) if (short) "${fn[0].uppercaseChar()}." else fn else ""}${if (mn.isNotBlank()) if (short) "${mn[0].uppercaseChar()}." else " $mn" else ""}".trim()
    }

    /**
     * Метод формирования имени пользователя в формате И.О. Фамилия
     * @param lastName фамилия
     * @param firstName имя
     * @param middleName отчество
     * @return строку в формате Фамилия И.О. или Фамилия Имя Отчество
     */
    fun userShortName(lastName: String?, firstName: String?, middleName: String?): String {
        val ln = (lastName ?: "").trim()
        val fn = (firstName ?: "").trim()
        val mn = (middleName ?: "").trim()
        return "${if (fn.isNotBlank()) "${fn[0].uppercaseChar()}." else ""}${if (mn.isNotBlank()) "${mn[0].uppercaseChar()}." else ""} $ln".trim()
    }

    /**
     * Метод для проверки разницы между датами
     * @param dateFrom с даты
     * @param dateTo по дату
     * @return true - если dateFrom больше dateTo, иначе false
     */
    fun dateFromMoreThenTo(dateFrom: LocalDate?, dateTo: LocalDate?) =
        dateFrom != null && dateTo != null && ChronoUnit.DAYS.between(dateFrom, dateTo) < 0

    /**
     * Метод преобразования файла в input stream
     * @param directory наименование директории в ресурсах проекта
     * @param fileName наименование файла
     * @return файл в виде [FileInputStream]
     */
    fun sourceFile(directory: String, fileName: String) =
        FileInputStream(DefaultResourceLoader().getResource("$CLASSPATH_URL_PREFIX$directory${File.separator}$fileName").file)

    /**
     * Добавление нулей в начало строки
     * @param len необходимая длина
     * @return строка с дополненными нулями
     */
    fun String?.padStartZero(len: Int) = this?.padStart(len, '0') ?: ""

    /**
     * Возвращает null если строка пуста, иначе возвращает саму строку
     * @return null или исходная строка
     */
    fun String?.nullIfBlank() = if (this.isNullOrBlank()) null else this

    /**
     * Возвращает null если строка пуста, иначе выполняет обработку
     * @param value обработчик строки, если она не пуста
     * @return null или обработанная строка
     */
    inline fun String?.nullIfBlankOr(value: (str: String) -> String?) = this.nullIfBlank()?.let(value)

    /**
     * Возвращает пустую строку, если строка равна null, иначе выполняет обработчик
     * @param value обработчик строки, если она не null
     * @return пустая строка или обработанная
     */
    inline fun String?.blankIfNullOr(value: (str: String) -> String = { it }) = if (this == null) "" else value(this)

    /**
     * Метод для вывода названия месяца в родительном падеже
     * @param  date дата
     * @return месяц в родительном падеже
     */
    fun LocalDate?.monthInGenitive(): String {
        if (this == null) return ""
        return GregorianCalendar(this.year, this.monthValue - ONE_INT, this.dayOfMonth).getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, Locale("ru")) ?: ""
    }

    /**
     * Метод возврата null, если список пуст
     * @return null или список
     */
    fun <T> List<T>?.nullIfEmpty() = if (this == null || this.isEmpty()) null else this
}