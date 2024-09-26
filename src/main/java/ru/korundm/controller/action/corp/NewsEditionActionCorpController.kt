package ru.korundm.controller.action.corp

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.SessionAttributes
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.FileStorageService
import ru.korundm.dao.NewsService
import ru.korundm.entity.News
import ru.korundm.entity.NewsType
import ru.korundm.form.NewsEditForm
import ru.korundm.helper.ValidatorResponse
import ru.korundm.helper.FileStorageType
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.TabrResultQuery
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
import ru.korundm.form.NewsListFilterForm as ListFilterForm


private const val LIST_FILTER_FORM_ATTR = "newsListFilterForm"
@ActionController([RequestPath.Action.Corp.NEWS_EDITION])
@SessionAttributes(names = [LIST_FILTER_FORM_ATTR], types = [ListFilterForm::class])
class NewsEditionActionCorpController(
    private val newsService: NewsService,
    private val jsonMapper: ObjectMapper,
    private val fileStorageService: FileStorageService
) {

    // Загрузка новостей
    @GetMapping("/list/load")
    fun listLoad(
        request: HttpServletRequest,
        model: ModelMap,
        filterForm: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?,
            val title: String, // заголовок
            val dateCreated: LocalDateTime?, // дата создания новости
            val topStatus: Boolean // закреплённая
        )
        val form = jsonMapper.readValue(filterForm, ListFilterForm::class.java)
        model.addAttribute(LIST_FILTER_FORM_ATTR, form)
        val input = TabrIn(request)
        val dataResultQuery: TabrResultQuery<News> = newsService.queryDataByFilterForm(input, form)
        val outList = dataResultQuery.data.map { news -> Item(
            news.id,
            news.title,
            news.dateCreated,
            news.topStatus
        ) }
        val output = TabrOut<Item>()
        output.currentPage = input.page
        output.setLastPage(input.size, dataResultQuery.count)
        output.data = outList
        return output
    }

    // Сохранение новости
    @PostMapping("/list/edit/save")
    fun listEditSave(form: NewsEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            (form.id?.let { newsService.read(it) } ?: News()).apply{
                title = form.title
                previewText = form.previewText
                detailText = form.detailText
                dateCreated = LocalDateTime.now()
                dateEventFrom = form.dateEventFrom
                dateEventTo = form.dateEventTo
                dateActiveFrom = form.dateActiveFrom
                dateActiveTo = form.dateActiveTo
                topStatus = form.topStatus
                type = NewsType(form.typeId)
                newsService.save(this)
                if (form.topStatus) {
                    newsService.clearTopStatus(this.id ?: 0)
                }
                if (form.file?.isEmpty == false) {
                    fileStorageService.saveEntityFile(this, FileStorageType.NewsParamFile, form.file)
                }
            }
        }
        return response
    }

    // Удаление новости
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = newsService.deleteById(id)
}