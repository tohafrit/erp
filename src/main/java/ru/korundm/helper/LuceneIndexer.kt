package ru.korundm.helper

import org.hibernate.search.jpa.FullTextEntityManager
import org.hibernate.search.jpa.Search
import javax.persistence.EntityManagerFactory

class LuceneIndexer(entityManagerFactory: EntityManagerFactory) {

    private val fullTextEntityManager: FullTextEntityManager = Search.getFullTextEntityManager(entityManagerFactory.createEntityManager())

    fun indexing() = fullTextEntityManager.createIndexer().startAndWait()
}