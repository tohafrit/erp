package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.TechnologicalEntity
import ru.korundm.entity.TechnologicalEntityNotification
import ru.korundm.entity.User
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.TechnologicalEntityNotificationRepository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface TechnologicalEntityNotificationService : CommonService<TechnologicalEntityNotification> {

    fun findTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<TechnologicalEntityNotification>
}

@Service
@Transactional
class TechnologicalEntityNotificationServiceImpl(
    private val repository: TechnologicalEntityNotificationRepository
) : TechnologicalEntityNotificationService {

    private val cl = TechnologicalEntityNotification::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<TechnologicalEntityNotification> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<TechnologicalEntityNotification> = repository.findAllById(idList)

    override fun save(obj: TechnologicalEntityNotification) = repository.save(obj)

    override fun saveAll(objectList: List<TechnologicalEntityNotification>): List<TechnologicalEntityNotification> = repository.saveAll(objectList)

    override fun read(id: Long): TechnologicalEntityNotification? = repository.findById(id).orElse(null)

    override fun delete(obj: TechnologicalEntityNotification) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<TechnologicalEntityNotification> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        val entityJoin = root.join<TechnologicalEntityNotification, TechnologicalEntity>(TechnologicalEntityNotification::technologicalEntity.name, JoinType.INNER)
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            val userJoin = root.join<TechnologicalEntityNotification, User>(TechnologicalEntityNotification::techUser.name, JoinType.INNER)
            when (it.field) {
                ObjAttr.DOC_NUMBER -> prSort(root.get<String>(TechnologicalEntityNotification::docNumber.name))
                ObjAttr.RELEASE_ON -> prSort(root.get<LocalDate>(TechnologicalEntityNotification::releaseOn.name))
                ObjAttr.TERM_CHANGE_ON -> prSort(root.get<LocalDate>(TechnologicalEntityNotification::termChangeOn.name))
                ObjAttr.REASON -> prSort(root.get<Long>(TechnologicalEntityNotification::reason.name))
                ObjAttr.TEXT -> prSort(root.get<String>(TechnologicalEntityNotification::text.name))
                ObjAttr.RESERVE_INDICATION -> prSort(root.get<Boolean>(TechnologicalEntityNotification::reserveIndication.name))
                ObjAttr.INTRODUCTION_INDICATION -> prSort(root.get<String>(TechnologicalEntityNotification::introductionIndication.name))
                ObjAttr.ENTITY_NUMBER -> prSort(entityJoin.get<String>(TechnologicalEntity::entityNumber.name))
                ObjAttr.TECH_USER -> prSort(userJoin.get<String>(ObjAttr.LAST_NAME))
                else -> prSort(root.get<Long>(TechnologicalEntityNotification::id.name))
            }
            cq.orderBy(orderList)
        }
        val predicateList = mutableListOf<Predicate>()
        // Номер извещения
        val docNumber = form.string(ObjAttr.DOC_NUMBER)
        if (!docNumber.isNullOrBlank()) predicateList += cb.like(root.get(TechnologicalEntityNotification::docNumber.name), "%$docNumber%")
        // Дата выпуска
        val pathReleaseOn = root.get<LocalDate>(ObjAttr.RELEASE_ON)
        form.date(ObjAttr.RELEASE_ON_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathReleaseOn, it) }
        form.date(ObjAttr.RELEASE_ON_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathReleaseOn, it) }
        // Срок изменений
        val pathTermChangeOn = root.get<LocalDate>(ObjAttr.TERM_CHANGE_ON)
        form.date(ObjAttr.TERM_CHANGE_ON_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathTermChangeOn, it) }
        form.date(ObjAttr.TERM_CHANGE_ON_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathTermChangeOn, it) }
        // Причина
        val reason = form.long(ObjAttr.REASON)
        reason?.let { predicateList += cb.equal(root.get<Long>(TechnologicalEntityNotification::reason.name), reason) }
        // Текст извещения
        val text = form.string(ObjAttr.TEXT)
        if (!text.isNullOrBlank()) predicateList += cb.like(root.get(TechnologicalEntityNotification::text.name), "%$text%")
        // Указание о заделе
        val pathReserveIndication = root.get<Boolean>(ObjAttr.RESERVE_INDICATION)
        form.bool(ObjAttr.RESERVE_INDICATION)?.let {
            predicateList += if (it) cb.isTrue(pathReserveIndication) else cb.isFalse(pathReserveIndication)
        }
        // Указание о внедрении
        val introductionIndication = form.string(ObjAttr.INTRODUCTION_INDICATION)
        if (!introductionIndication.isNullOrBlank())
            predicateList += cb.like(root.get(TechnologicalEntityNotification::introductionIndication.name), "%$introductionIndication%")
        // Номер технологической документации
        val technologicalEntity = form.string(ObjAttr.TECHNOLOGICAL_ENTITY)
        if (!technologicalEntity.isNullOrBlank()) {
            predicateList += cb.like(entityJoin.get(TechnologicalEntity::entityNumber.name), "%$technologicalEntity%")
        }
        // Технолог
        form.long(ObjAttr.TECH_USER_ID)?.let {
            predicateList += cb.equal(root.get<Long>(TechnologicalEntityNotification::techUser.name), it)
        }

        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}