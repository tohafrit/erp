package ru.korundm.dao

import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.CompanyDetailM
import ru.korundm.entity.OfficeSupply
import ru.korundm.entity.OfficeSupplyM
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.helper.TabrSorter
import ru.korundm.repository.OfficeSupplyRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import ru.korundm.form.OfficeSupplyListFilterForm as ListFilterForm

interface OfficeSupplyService : CommonService<OfficeSupply> {

    fun queryDataByFilterForm(tableDataIn: TabrIn, form: ListFilterForm): TabrResultQuery<OfficeSupply>
}

@Service
@Transactional
class OfficeSupplyServiceImpl(
    private val officeSupplyRepository: OfficeSupplyRepository
) : OfficeSupplyService {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun getAll(): List<OfficeSupply> = officeSupplyRepository.findAll()

    override fun getAllById(idList: List<Long>): List<OfficeSupply> = officeSupplyRepository.findAllById(idList)

    override fun save(obj: OfficeSupply): OfficeSupply = officeSupplyRepository.save(obj)

    override fun saveAll(objectList: List<OfficeSupply>): List<OfficeSupply> = officeSupplyRepository.saveAll(objectList)

    override fun read(id: Long): OfficeSupply? = officeSupplyRepository.findById(id).orElse(null)

    override fun delete(obj: OfficeSupply) = officeSupplyRepository.delete(obj)

    override fun deleteById(id: Long) = officeSupplyRepository.deleteById(id)

    override fun queryDataByFilterForm(
        tableDataIn: TabrIn,
        form: ListFilterForm
    ): TabrResultQuery<OfficeSupply> {
        val cb = entityManager.criteriaBuilder
        val cqData = cb.createQuery(OfficeSupply::class.java)
        val root = cqData.from(OfficeSupply::class.java)
        val selectData = cqData.select(root)
        cqData.where(*predicateListByFilterForm(form, root, cb).toTypedArray())
        val sorterList: List<TabrSorter> = tableDataIn.sorters
        if (sorterList.isNotEmpty()) {
            val sorter: TabrSorter = sorterList[0]
            val orderExpressionList = mutableListOf<Expression<*>>()
            orderExpressionList += when (sorter.field) {
                OfficeSupplyM.ARTICLE -> root.get<String>(OfficeSupplyM.ARTICLE)
                OfficeSupplyM.NAME -> root.get<String>(OfficeSupplyM.NAME)
                OfficeSupplyM.ACTIVE -> root.get(OfficeSupplyM.ACTIVE)
                OfficeSupplyM.ONLY_SECRETARIES -> root.get(OfficeSupplyM.ONLY_SECRETARIES)
                else -> root.get<Long>(CompanyDetailM.ID)
            }
            cqData.orderBy(orderExpressionList.map(if (ASC == sorter.dir) cb::asc else cb::desc).toList())
        }
        val tqData = entityManager.createQuery(selectData)
        tqData.firstResult = tableDataIn.start
        tqData.maxResults = tableDataIn.size
        val cbCount = entityManager.criteriaBuilder
        val cCount = cbCount.createQuery(Long::class.java)
        val rootCount  = cCount.from(OfficeSupply::class.java)
        cCount.select(cbCount.count(rootCount)).where(*predicateListByFilterForm(form, rootCount, cbCount).toTypedArray())
        return TabrResultQuery(tqData.resultList, entityManager.createQuery(cCount).singleResult)
    }

    private fun predicateListByFilterForm(
        form: ListFilterForm,
        root: Root<OfficeSupply>,
        cb: CriteriaBuilder
    ): List<Predicate> {
        val predicateList = mutableListOf<Predicate>()
        //
        val article= form.article
        if (article.isNotBlank()) predicateList += cb.like(root[OfficeSupplyM.ARTICLE], "%$article%")
        //
        val name = form.name
        if (name.isNotBlank()) predicateList += cb.like(root[OfficeSupplyM.NAME], "%$name%")
        //
        val active = form.active
        if (active != null) predicateList += cb.equal(root.get<Boolean>(OfficeSupplyM.ACTIVE), active)
        //
        val onlySecretaries = form.onlySecretaries
        if (onlySecretaries != null) predicateList += cb.equal(root.get<Boolean>(OfficeSupplyM.ONLY_SECRETARIES), onlySecretaries)
        return predicateList
    }
}