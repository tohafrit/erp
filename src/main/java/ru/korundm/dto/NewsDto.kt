package ru.korundm.dto

import java.time.LocalDate

class NewsDto(

    var id: Long? = null,
    var title: String, // заголовок
    var previewText: String, // текст анонса
    var detailText: String?, // текст содержимого
    var date: LocalDate? = null, // дата создания новости
    var fileUrlHash: String? // хеш url изображение
)