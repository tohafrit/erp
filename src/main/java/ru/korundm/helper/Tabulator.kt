package ru.korundm.helper

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Direction.DESC
import ru.korundm.util.KtCommonUtil.rowCount
import javax.servlet.http.HttpServletRequest

/**
 * Класс для хранения значений фильтра для tabulator
 * @author mazur_ea
 * Date:   06.07.2020
 */
class TabrFilter(val field: String = "", val type: String = "", val value: String = "")

/**
 * Класс для хранения данных результата запроса для tabulator
 * @author mazur_ea
 * Date:   14.06.2020
 */
class TabrResultQuery<T>(val data: List<T>, val count: Long) {

    companion object {
        fun <T : RowCountable> instance(data: List<T>) = TabrResultQuery(data, data.rowCount())
    }
}

/**
 * Класс для хранения значений сортировки для tabulator
 * @author mazur_ea
 * Date:   14.06.2020
 */
class TabrSorter(val field: String = "", val dir: Direction = ASC)

/**
 * Класс хранения результатов ajax-ответа для tabulator
 * @author mazur_ea
 * Date:   12.05.2020
 */
class TabrOut<T> {

    companion object {

        fun <R, T> instance(input: TabrIn, result: TabrResultQuery<R>, transform: (item: R) -> T): TabrOut<T> {
            val output = TabrOut<T>()
            output.currentPage = input.page
            output.setLastPage(input.size, result.count)
            output.data = result.data.map { transform(it) }
            return output
        }

        fun <R> instance(input: TabrIn, result: TabrResultQuery<R>) = instance(input, result) { res -> res }
    }

    @JsonProperty("current_page")
    var currentPage = 1 // текущая страница
    @JsonProperty("last_page")
    var lastPage = 1 // последняя страница
        private set
    var data = emptyList<T>() // данные таблицы

    fun setLastPage(size: Int, totalSize: Long) {
        if (size == 0) return
        lastPage = (totalSize / size).toInt()
        lastPage = if (totalSize % size == 0L) lastPage else lastPage + 1
    }
}

/**
 * Класс извлечения и хранения параметров ajax-запроса для tabulator
 * @author mazur_ea
 * Date:   12.05.2020
 */
class TabrIn(val request: HttpServletRequest) {

    var page = 0 // номер страницы
    var size = 0 // строк на страницу
    var start = 0 // строка начала страницы
    val sorters = mutableListOf<TabrSorter>() // список объектов сортировки
    val filters = mutableListOf<TabrFilter>() // список объектов фильтрации
    val sorter
        get() = sorters.firstOrNull()

    init {
        page = request.getParameter(::page.name)?.toIntOrNull() ?: page
        size = request.getParameter(::size.name)?.toIntOrNull() ?: size
        size = if (size > 200) 200 else size
        start = (page - 1) * size
        var i = 0
        while (true) {
            val field = request.getParameter("${::sorters.name}[$i][${TabrSorter::field.name}]")
            val dir = request.getParameter("${::sorters.name}[$i][${TabrSorter::dir.name}]")
            if (field === null) break else sorters += TabrSorter(field, when(dir.uppercase()) { DESC.name -> DESC else -> ASC })
            i++
        }
        i = 0
        while (true) {
            val field = request.getParameter("${::filters.name}[$i][${TabrFilter::field.name}]")
            val type = request.getParameter("${::filters.name}[$i][${TabrFilter::type.name}]")
            val value = request.getParameter("${::filters.name}[$i][${TabrFilter::value.name}]")
            if (field === null) break else filters += TabrFilter(field, type, value)
            i++
        }
    }
}