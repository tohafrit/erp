package ru.korundm.controller.view.corp

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.SessionAttributes
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.FileStorageService
import ru.korundm.dao.NewsService
import ru.korundm.dao.NewsTypeService
import ru.korundm.form.NewsEditForm
import ru.korundm.helper.FileStorageType
import ru.korundm.form.NewsListFilterForm as ListFilterForm

private const val LIST_FILTER_FORM_ATTR = "newsListFilterForm"
@ViewController([RequestPath.View.Corp.NEWS_EDITION])
@SessionAttributes(names = [LIST_FILTER_FORM_ATTR], types = [ListFilterForm::class])
class NewsEditionViewCorpController(
    private val newsService: NewsService,
    private val newsTypeService: NewsTypeService,
    private val fileStorageService: FileStorageService
) {

    @ModelAttribute(LIST_FILTER_FORM_ATTR)
    fun listFilterForm() = ListFilterForm()

    @GetMapping("/list")
    fun list() = "corp/include/news-edition/list"

    @GetMapping("/list/detail")
    fun listDetail(model: ModelMap, id: Long): String {
        val news =  newsService.read(id)
        model.addAttribute("news", news)
        model.addAttribute("image", fileStorageService.readOneSingular(news, FileStorageType.NewsParamFile))
        return "corp/include/news-edition/list/detail"
    }

    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val form = NewsEditForm()
        id?.let {
            val news = newsService.read(it)
            form.id = it
            form.title = news.title
            form.previewText = news.previewText
            form.detailText = news.detailText
            form.dateEventFrom = news.dateEventFrom
            form.dateEventTo = news.dateEventTo
            form.dateActiveFrom = news.dateActiveFrom
            form.dateActiveTo = news.dateActiveTo
            form.topStatus = news.topStatus
            form.typeId = news.type?.id
            form.fileStorage = fileStorageService.readOneSingular(news, FileStorageType.NewsParamFile)
        }
        model["form"] = form
        model["typeList"] = newsTypeService.all
        return "corp/include/news-edition/list/edit"
    }
}