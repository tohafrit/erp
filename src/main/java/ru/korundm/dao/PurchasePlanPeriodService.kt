package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.PurchasePlanPeriod
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.PurchasePlanPeriodRepository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface PurchasePlanPeriodService : CommonService<PurchasePlanPeriod> {

    fun findTableData(input: TabrIn, form: DynamicObject): TabrResultQuery<PurchasePlanPeriod>
    fun findLastPeriod(): PurchasePlanPeriod?
}

@Service
@Transactional
class PurchasePlanPeriodServiceImpl(
    private val repository: PurchasePlanPeriodRepository
) : PurchasePlanPeriodService {

    private val cl = PurchasePlanPeriod::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<PurchasePlanPeriod> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<PurchasePlanPeriod> = repository.findAllById(idList)

    override fun save(obj: PurchasePlanPeriod): PurchasePlanPeriod {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<PurchasePlanPeriod>): List<PurchasePlanPeriod> = repository.saveAll(objectList)

    override fun read(id: Long): PurchasePlanPeriod? = repository.findById(id).orElse(null)

    override fun delete(obj: PurchasePlanPeriod) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<PurchasePlanPeriod> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.NUMBER -> prSort(root.get<Int>(PurchasePlanPeriod::number.name))
                ObjAttr.CREATE_DATE -> prSort(root.get<LocalDate>(PurchasePlanPeriod::createDate.name))
                ObjAttr.FIRST_DATE -> prSort(root.get<LocalDate>(PurchasePlanPeriod::firstDate.name))
                ObjAttr.LAST_DATE -> prSort(root.get<LocalDate>(PurchasePlanPeriod::lastDate.name))
                else -> prSort(root.get<Long>(ObjAttr.ID))
            }
            cq.orderBy(orderList)
        }
        val predicateList = mutableListOf<Predicate>()
        // Номер периода
        form.int(ObjAttr.NUMBER)?.let { predicateList += cb.equal(root.get<Int>(PurchasePlanPeriod::number.name), it) }
        // Дата создания
        val createDate = root.get<LocalDate>(PurchasePlanPeriod::createDate.name)
        form.date(ObjAttr.CREATE_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(createDate, it) }
        form.date(ObjAttr.CREATE_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(createDate, it) }
        // Дата начала периода
        val firstDate = root.get<LocalDate>(PurchasePlanPeriod::firstDate.name)
        form.date(ObjAttr.FIRST_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(firstDate, it) }
        form.date(ObjAttr.FIRST_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(firstDate, it) }
        // Дата окончания периода
        val lastDate = root.get<LocalDate>(PurchasePlanPeriod::lastDate.name)
        form.date(ObjAttr.LAST_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(lastDate, it) }
        form.date(ObjAttr.LAST_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(lastDate, it) }

        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun findLastPeriod(): PurchasePlanPeriod? {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        return em.createQuery(cq.select(root).orderBy(
            cb.desc(root.get<Int>(PurchasePlanPeriod::year.name)),
            cb.desc(root.get<Int>(PurchasePlanPeriod::number.name))
        )).setMaxResults(ONE_INT).resultList.singleOrNull()
    }
}