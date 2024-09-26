package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.BasicEconomicIndicator
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.BasicEconomicIndicatorRepository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface BasicEconomicIndicatorService : CommonService<BasicEconomicIndicator> {

    fun findTableData(tableInput: TabrIn, form: DynamicObject): TabrResultQuery<BasicEconomicIndicator>
    fun findLastByApproveDate(): BasicEconomicIndicator?
}

@Service
@Transactional
class BasicEconomicIndicatorServiceImpl(
    private val repository: BasicEconomicIndicatorRepository
) : BasicEconomicIndicatorService {

    private val cl = BasicEconomicIndicator::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<BasicEconomicIndicator> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<BasicEconomicIndicator> = repository.findAllById(idList)

    override fun save(obj: BasicEconomicIndicator): BasicEconomicIndicator {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<BasicEconomicIndicator>): List<BasicEconomicIndicator> = repository.saveAll(objectList)

    override fun read(id: Long): BasicEconomicIndicator? = repository.findById(id).orElse(null)

    override fun delete(obj: BasicEconomicIndicator) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(tableInput: TabrIn, form: DynamicObject): TabrResultQuery<BasicEconomicIndicator> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        val predicateList = mutableListOf<Predicate>()
        form.string(ObjAttr.NAME)?.let { predicateList += cb.like(root.get(BasicEconomicIndicator::name.name), "%$it%") }
        form.string(ObjAttr.DOC_NAME)?.let { predicateList += cb.like(root.get(BasicEconomicIndicator::docName.name), "%$it%") }
        val pathApprovalDate = root.get<LocalDate>(BasicEconomicIndicator::approvalDate.name)
        form.date(ObjAttr.APPROVAL_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathApprovalDate, it) }
        form.date(ObjAttr.APPROVAL_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathApprovalDate, it) }

        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.NAME -> prSort(root.get<String>(BasicEconomicIndicator::name.name))
                ObjAttr.DOC_NAME -> prSort(root.get<String>(BasicEconomicIndicator::docName.name))
                ObjAttr.APPROVAL_DATE -> prSort(root.get<LocalDate>(BasicEconomicIndicator::approvalDate.name))
                ObjAttr.ADDITIONAL_SALARY -> prSort(root.get<Double>(BasicEconomicIndicator::additionalSalary.name))
                ObjAttr.SOCIAL_INSURANCE -> prSort(root.get<Double>(BasicEconomicIndicator::socialInsurance.name))
                ObjAttr.OVERHEAD_COSTS -> prSort(root.get<Double>(BasicEconomicIndicator::overheadCosts.name))
                ObjAttr.PRODUCTION_COSTS -> prSort(root.get<Double>(BasicEconomicIndicator::productionCosts.name))
                ObjAttr.HOUSEHOLD_EXPENSES -> prSort(root.get<Double>(BasicEconomicIndicator::householdExpenses.name))
                else -> prSort(root.get<Long>(ObjAttr.ID))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = tableInput.start
        typedQuery.maxResults = tableInput.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun findLastByApproveDate() = repository.findFirstByOrderByApprovalDateDesc()
}