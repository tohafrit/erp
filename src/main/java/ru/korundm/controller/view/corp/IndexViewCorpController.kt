package ru.korundm.controller.view.corp

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.FileStorageService
import ru.korundm.dao.NewsService
import ru.korundm.dto.NewsDto
import ru.korundm.helper.FileStorageType
import ru.korundm.util.FileStorageUtil.extractSingular

@ViewController([RequestPath.View.Corp.INDEX])
class IndexViewCorpController(
    private val newsService: NewsService,
    private val fileStorageService: FileStorageService
) {

    // Новости
    @GetMapping("/news")
    fun news(model: ModelMap): String {
        val newsList = newsService.getFresh()
        val fileList = fileStorageService.readAny(newsList)
        model["newsList"] = newsList.map { news -> NewsDto(
            news.id,
            news.title,
            news.previewText,
            news.detailText,
            news.date?.toLocalDate(),
            fileList.extractSingular(news, FileStorageType.NewsParamFile)?.urlHash
        ) }
        return "corp/include/index/news"
    }
}