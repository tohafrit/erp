package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.ConstructorDocumentNotification
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.ConstructorDocumentNotificationRepository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface ConstructorDocumentNotificationService: CommonService<ConstructorDocumentNotification> {

    fun getAllByApplicabilityProduct(id: Long): List<ConstructorDocumentNotification>

    fun findTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<ConstructorDocumentNotification>
}

@Service
@Transactional
class ConstructorDocumentNotificationServiceImpl(
    private val repository: ConstructorDocumentNotificationRepository
) : ConstructorDocumentNotificationService {

    private val cl = ConstructorDocumentNotification::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ConstructorDocumentNotification> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ConstructorDocumentNotification> = repository.findAllById(idList)

    override fun save(obj: ConstructorDocumentNotification): ConstructorDocumentNotification {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ConstructorDocumentNotification>): List<ConstructorDocumentNotification> = repository.saveAll(objectList)

    override fun read(id: Long): ConstructorDocumentNotification? = repository.findById(id).orElse(null)

    override fun delete(obj: ConstructorDocumentNotification) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun getAllByApplicabilityProduct(id: Long) = repository.findAllByApplicabilityProductIdOrderByTermChangeOnDesc(id)

    override fun findTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<ConstructorDocumentNotification> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.DOC_NUMBER -> prSort(root.get<String>(ConstructorDocumentNotification::docNumber.name))
                ObjAttr.RELEASE_ON -> prSort(root.get<LocalDate>(ConstructorDocumentNotification::releaseOn.name))
                ObjAttr.TERM_CHANGE_ON -> prSort(root.get<LocalDate>(ConstructorDocumentNotification::termChangeOn.name))
                ObjAttr.REASON -> prSort(root.get<String>(ConstructorDocumentNotification::reason.name))
                ObjAttr.RESERVE_INDICATION -> prSort(root.get<Boolean>(ConstructorDocumentNotification::reserveIndication.name))
                ObjAttr.INTRODUCTION_INDICATION -> prSort(root.get<String>(ConstructorDocumentNotification::introductionIndication.name))
                else -> prSort(root.get<Long>(ConstructorDocumentNotification::id.name))
            }
            cq.orderBy(orderList)
        }
        val predicateList = mutableListOf<Predicate>()
        form.string(ObjAttr.DOC_NUMBER)?.let { predicateList += cb.like(root.get(ConstructorDocumentNotification::docNumber.name), "%$it%") }
        form.date(ObjAttr.RELEASE_ON)?.let { predicateList += cb.equal(root.get<LocalDate>(ConstructorDocumentNotification::releaseOn.name), it) }
        form.date(ObjAttr.TERM_CHANGE_ON)?.let { predicateList += cb.equal(root.get<LocalDate>(ConstructorDocumentNotification::termChangeOn.name), it) }
        form.string(ObjAttr.REASON)?.let { predicateList += cb.like(root.get(ConstructorDocumentNotification::reason.name), "%$it%") }
        form.bool(ObjAttr.RESERVE_INDICATION)?.let { predicateList += cb.equal(root.get<Boolean>(ConstructorDocumentNotification::reserveIndication.name), it) }
        form.string(ObjAttr.INTRODUCTION_INDICATION)?.let { predicateList += cb.like(root.get(ConstructorDocumentNotification::introductionIndication.name), "%$it%") }
        val notificationIdList = form.listLong(ObjAttr.NOTIFICATION_ID_LIST)
        if (notificationIdList.isNotEmpty()) predicateList += cb.not(root.get<Long>(ConstructorDocumentNotification::id.name).`in`(notificationIdList))
        val includeChild = form.bool(ObjAttr.INCLUDE_CHILD)
        if (includeChild == null || includeChild == false) predicateList += cb.isNull(root.get<ConstructorDocumentNotification>(ConstructorDocumentNotification::parent.name))

        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}