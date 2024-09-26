package ru.korundm.dao

import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.News
import ru.korundm.entity.NewsM
import ru.korundm.form.NewsListFilterForm
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.helper.TabrSorter
import ru.korundm.repository.NewsRepository
import java.time.LocalDateTime
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.*

interface NewsService : CommonService<News> {

    fun clearTopStatus(id: Long = 0)

    fun getFresh(): List<News>

    fun getTop(): News?

    fun getByPageNumber(page: Int = 0): List<News>

    fun getPages(): Int

    fun queryDataByFilterForm(tableDataIn: TabrIn, form: NewsListFilterForm): TabrResultQuery<News>
}

private const val MAIN_PAGE_NEWS = 3 // число новостей на главной странице
private const val NEWS_PER_PAGE = 5 // число новостей, выводимых одновременно на страницу новостей

@Service
@Transactional
class NewsServiceImpl(
    private val newsRepository: NewsRepository
) : NewsService {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun getAll(): List<News> = newsRepository.findAll()

    override fun getAllById(idList: List<Long>): List<News> = newsRepository.findAllById(idList)

    override fun save(obj: News) = newsRepository.save(obj)

    override fun saveAll(objectList: List<News>): List<News> = newsRepository.saveAll(objectList)

    override fun read(id: Long): News? = newsRepository.findById(id).orElse(null)

    override fun delete(obj: News) = newsRepository.delete(obj)

    override fun deleteById(id: Long) = newsRepository.deleteById(id)

    override fun clearTopStatus(id: Long) {
        val topNewsList = newsRepository.findAllByIdIsNotAndTopStatusTrue(id)
        topNewsList.forEach { it.topStatus = false }
        newsRepository.saveAll(topNewsList)
    }

    override fun getFresh(): List<News> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(News::class.java)
        val root = criteriaQuery.from(News::class.java)
        val select = criteriaQuery.select(root)
        criteriaQuery.where(*predicateList(root, criteriaBuilder).toTypedArray())
        val orderExpressionList = mutableListOf<Expression<*>>()
        orderExpressionList += criteriaBuilder.coalesce<LocalDateTime>(root.get(NewsM.DATE_ACTIVE_FROM), root.get(NewsM.DATE_CREATED))
        criteriaQuery.orderBy(orderExpressionList.map(criteriaBuilder::desc).toList())
        val typedQuery = entityManager.createQuery(select)
        typedQuery.maxResults = MAIN_PAGE_NEWS
        return typedQuery.resultList
    }

    override fun getTop(): News? {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(News::class.java)
        val root = criteriaQuery.from(News::class.java)
        val select = criteriaQuery.select(root)
        val predicateList = predicateList(root, criteriaBuilder).toMutableList()
        predicateList += criteriaBuilder.isTrue(root[NewsM.TOP_STATUS])
        criteriaQuery.where(*predicateList.toTypedArray())
        val orderExpressionList = mutableListOf<Expression<*>>()
        orderExpressionList += criteriaBuilder.coalesce<LocalDateTime>(root.get(NewsM.DATE_ACTIVE_FROM), root.get(NewsM.DATE_CREATED))
        criteriaQuery.orderBy(orderExpressionList.map(criteriaBuilder::desc).toList())
        return entityManager.createQuery(select).setMaxResults(1).resultList.singleOrNull()
    }

    override fun getByPageNumber(page: Int): List<News> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(News::class.java)
        val root = criteriaQuery.from(News::class.java)
        val select = criteriaQuery.select(root)
        val predicateList = predicateList(root, criteriaBuilder)
        predicateList += criteriaBuilder.isFalse(root[NewsM.TOP_STATUS])
        criteriaQuery.where(*predicateList.toTypedArray())
        val orderExpressionList = mutableListOf<Expression<*>>()
        orderExpressionList += criteriaBuilder.coalesce<LocalDateTime>(root.get(NewsM.DATE_ACTIVE_FROM), root.get(NewsM.DATE_CREATED))
        criteriaQuery.orderBy(orderExpressionList.map(criteriaBuilder::desc).toList())
        val typedQuery = entityManager.createQuery(select)
        typedQuery.firstResult = page * NEWS_PER_PAGE
        typedQuery.maxResults = NEWS_PER_PAGE
        return typedQuery.resultList
    }

    override fun getPages(): Int {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Long::class.java)
        val root = criteriaQuery.from(News::class.java)
        criteriaQuery.select(criteriaBuilder.count(root))
        criteriaQuery.where(*predicateList(root, criteriaBuilder).toTypedArray())
        val count = entityManager.createQuery(criteriaQuery).singleResult
        return ((count + NEWS_PER_PAGE - 1) / NEWS_PER_PAGE).toInt()
    }

    private fun predicateList(
        root: Root<News>,
        criteriaBuilder: CriteriaBuilder
    ): MutableList<Predicate> {
        val predicateList = mutableListOf<Predicate>()
        predicateList += criteriaBuilder.or(
            criteriaBuilder.lessThanOrEqualTo(root[NewsM.DATE_ACTIVE_FROM], criteriaBuilder.currentTimestamp()),
            root.get<String>(NewsM.DATE_ACTIVE_FROM).isNull
        )
        predicateList += criteriaBuilder.or(
            criteriaBuilder.greaterThanOrEqualTo(root[NewsM.DATE_ACTIVE_TO], criteriaBuilder.currentTimestamp()),
            root.get<String>(NewsM.DATE_ACTIVE_TO).isNull
        )
        return predicateList
    }

    override fun queryDataByFilterForm(
        tableDataIn: TabrIn,
        form: NewsListFilterForm
    ) : TabrResultQuery<News> {
        val cb = entityManager.criteriaBuilder
        val cqData = cb.createQuery(News::class.java)
        val root = cqData.from(News::class.java)
        val selectData = cqData.select(root)
        cqData.where(*predicateListByFilterForm(form, root, cb).toTypedArray())
        val sorterList: List<TabrSorter> = tableDataIn.sorters
        if (sorterList.isNotEmpty()) {
            val sorter: TabrSorter = sorterList[0]
            val orderExpressionList = mutableListOf<Expression<*>>()
            orderExpressionList += when (sorter.field) {
                NewsM.TITLE -> root.get<String>(NewsM.TITLE)
                NewsM.DATE_CREATED -> root.get<LocalDateTime>(NewsM.DATE_CREATED)
                else -> root.get<Long>(NewsM.ID)
            }
            cqData.orderBy(orderExpressionList.map(if (ASC == sorter.dir) cb::asc else cb::desc).toList())
        }
        val tqData = entityManager.createQuery(selectData)
        tqData.firstResult = tableDataIn.start
        tqData.maxResults = tableDataIn.size
        val cbCount = entityManager.criteriaBuilder
        val cCount = cbCount.createQuery(Long::class.java)
        val rootCount  = cCount.from(News::class.java)
        cCount.select(cbCount.count(rootCount)).where(*predicateListByFilterForm(form, rootCount, cbCount).toTypedArray())
        return TabrResultQuery(tqData.resultList, entityManager.createQuery(cCount).singleResult)
    }

    private fun predicateListByFilterForm(
        form: NewsListFilterForm,
        root: Root<News>,
        cb: CriteriaBuilder
    ): List<Predicate> {
        val predicateList = mutableListOf<Predicate>()

        val title = form.title
        if (title.isNotBlank()) predicateList += cb.like(root[NewsM.TITLE], "%$title%")

        val previewText = form.previewText
        if (previewText.isNotBlank()) predicateList += cb.like(root[NewsM.PREVIEW_TEXT], "%$previewText%")

        val detailText = form.detailText
        if (detailText.isNotBlank()) predicateList += cb.like(root[NewsM.DETAIL_TEXT], "%$detailText%")

        if (form.topStatus) predicateList += cb.isTrue(root[NewsM.TOP_STATUS])

        val requestDate: Path<LocalDateTime> = root.get(NewsM.DATE_CREATED)
        val requestDateFrom: LocalDateTime? = form.dateCreatedFrom
        if (requestDateFrom != null) predicateList += cb.greaterThanOrEqualTo(requestDate, requestDateFrom)
        val requestDateTo: LocalDateTime? = form.dateCreatedTo
        if (requestDateTo != null) predicateList += cb.lessThanOrEqualTo(requestDate, requestDateTo)

        return predicateList
    }
}