package ru.korundm.controller.view.corp

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.FileStorageService
import ru.korundm.dao.NewsService
import ru.korundm.dto.NewsDto
import ru.korundm.helper.FileStorageType
import ru.korundm.util.FileStorageUtil.extractSingular

@ViewController([RequestPath.View.Corp.NEWS])
class NewsViewCorpController(
    private val newsService: NewsService,
    private val fileStorageService: FileStorageService
) {

    @GetMapping("/list")
    fun list(model: ModelMap): String {
        val topNews = newsService.getTop()
        if (topNews != null) {
            val image = fileStorageService.readOneSingular(topNews, FileStorageType.NewsParamFile)
            model["topNews"] = NewsDto(
                topNews.id,
                topNews.title,
                topNews.previewText,
                topNews.detailText,
                topNews.date?.toLocalDate(),
                image?.urlHash
            )
        }
        val newsList = newsService.getByPageNumber()
        val fileList = fileStorageService.readAny(newsList)
        model["newsList"] = newsList.map { news -> NewsDto(
            news.id,
            news.title,
            news.previewText,
            news.detailText,
            news.date?.toLocalDate(),
            fileList.extractSingular(news, FileStorageType.NewsParamFile)?.urlHash
        ) }
        model["pages"] = newsService.getPages()
        return "corp/include/news/list"
    }

    @GetMapping("/list/load")
    fun listLoad(model: ModelMap, page: Int): String {
        val newsList = newsService.getByPageNumber(page)
        val fileList = fileStorageService.readAny(newsList)
        model["newsList"] = newsList.map { news -> NewsDto(
            news.id,
            news.title,
            news.previewText,
            news.detailText,
            news.date?.toLocalDate(),
            fileList.extractSingular(news, FileStorageType.NewsParamFile)?.urlHash
        ) }
        return "corp/include/news/list/load"
    }

    @GetMapping("/list/{id}")
    fun list(model: ModelMap, @PathVariable id: Long): String {
        val news = newsService.read(id)
        if (news != null) {
            val image = fileStorageService.readOneSingular(news, FileStorageType.NewsParamFile)
            model["news"] = NewsDto(
                news.id,
                news.title,
                news.previewText,
                news.detailText,
                news.date?.toLocalDate(),
                image?.urlHash
            )
        }
        return "corp/include/news/list/id"
    }
}