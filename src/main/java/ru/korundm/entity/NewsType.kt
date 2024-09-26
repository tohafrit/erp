package ru.korundm.entity

import javax.persistence.*

@Entity
@Table(name = "news_types")
data class NewsType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Column(name = "name", nullable = false)
    var name = "" // наименование

    @OneToMany(mappedBy = "type")
    var newsList = mutableListOf<News>() // список новостей
}