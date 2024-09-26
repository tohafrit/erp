package ru.korundm.entity

import ru.korundm.helper.FileStorable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "news")
data class News(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    val id: Long? = null
) : FileStorable<News> {

    @Column(name = "title", nullable = false)
    var title = "" // заголовок

    @Column(name = "preview_text", nullable = false)
    var previewText = "" // текст анонса

    @Column(name = "detail_text")
    var detailText = "" // текст содержимого

    @Column(name = "date_created", nullable = false)
    var dateCreated: LocalDateTime? = null // дата создания новости

    @Column(name = "date_event_from")
    var dateEventFrom: LocalDateTime? = null // дата события с

    @Column(name = "date_event_to")
    var dateEventTo: LocalDateTime? = null // дата события по

    @Column(name = "date_active_from")
    var dateActiveFrom: LocalDateTime? = null // дата начала активности новости

    @Column(name = "date_active_to")
    var dateActiveTo: LocalDateTime? = null // дата окончания активности новости

    @Column(name = "top_status", nullable = false)
    var topStatus = false // статус закрепления новости вверху списка

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    var type: NewsType? = null // тип новости

    val date: LocalDateTime? // дата публикации: дата начала активности, если не null, иначе дата создания
        get() = dateActiveFrom ?: dateCreated

    val typeName: String // наименование типа новости, если тип не null, иначе пустая строка
        get() = type?.name ?: ""

    override fun storableId() = this.id

    override fun storableClass() = News::class
}

@Suppress("unused")
object NewsM {
    const val ID = "id"
    const val TITLE = "title"
    const val PREVIEW_TEXT = "previewText"
    const val DETAIL_TEXT = "detailText"
    const val DATE_CREATED = "dateCreated"
    const val DATE_EVENT_FROM = "dateEventFrom"
    const val DATE_EVENT_TO = "dateEventTo"
    const val DATE_ACTIVE_FROM = "dateActiveFrom"
    const val DATE_ACTIVE_TO = "dateActiveTo"
    const val TOP_STATUS = "topStatus"
    const val TYPE = "type"
}