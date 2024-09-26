package ru.korundm.dao

import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.CompanyDetail
import ru.korundm.entity.CompanyDetailM
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.helper.TabrSorter
import ru.korundm.repository.CompanyDetailRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import ru.korundm.form.CompanyDetailListFilterForm as ListFilterForm

interface CompanyDetailService : CommonService<CompanyDetail> {

    fun queryDataByFilterForm(tableDataIn: TabrIn, form: ListFilterForm): TabrResultQuery<CompanyDetail>
}

@Service
@Transactional
class CompanyDetailServiceImpl(
    private val companyDetailRepository: CompanyDetailRepository
) : CompanyDetailService {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun getAll(): List<CompanyDetail> = companyDetailRepository.findAll()

    override fun getAllById(idList: List<Long>): List<CompanyDetail> = companyDetailRepository.findAllById(idList)

    override fun save(obj: CompanyDetail): CompanyDetail = companyDetailRepository.save(obj)

    override fun saveAll(objectList: List<CompanyDetail>): List<CompanyDetail> = companyDetailRepository.saveAll(objectList)

    override fun read(id: Long): CompanyDetail? = companyDetailRepository.findById(id).orElse(null)

    override fun delete(obj: CompanyDetail) = companyDetailRepository.delete(obj)

    override fun deleteById(id: Long) = companyDetailRepository.deleteById(id)

    override fun queryDataByFilterForm(
        tableDataIn: TabrIn,
        form: ListFilterForm
    ) : TabrResultQuery<CompanyDetail> {
        val cb = entityManager.criteriaBuilder
        val cqData = cb.createQuery(CompanyDetail::class.java)
        val root = cqData.from(CompanyDetail::class.java)
        val selectData = cqData.select(root)
        cqData.where(*predicateListByFilterForm(form, root, cb).toTypedArray())
        val sorterList: List<TabrSorter> = tableDataIn.sorters
        if (sorterList.isNotEmpty()) {
            val sorter: TabrSorter = sorterList[0]
            val orderExpressionList = mutableListOf<Expression<*>>()
            orderExpressionList += when (sorter.field) {
                CompanyDetailM.NAME -> root.get<String>(CompanyDetailM.NAME)
                CompanyDetailM.VALUE -> root.get<String>(CompanyDetailM.VALUE)
                CompanyDetailM.SORT -> root.get<Int>(CompanyDetailM.SORT)
                else -> root.get<Long>(CompanyDetailM.ID)
            }
            cqData.orderBy(orderExpressionList.map(if (ASC == sorter.dir) cb::asc else cb::desc).toList())
        }
        val tqData = entityManager.createQuery(selectData)
        tqData.firstResult = tableDataIn.start
        tqData.maxResults = tableDataIn.size
        val cbCount = entityManager.criteriaBuilder
        val cCount = cbCount.createQuery(Long::class.java)
        val rootCount  = cCount.from(CompanyDetail::class.java)
        cCount.select(cbCount.count(rootCount)).where(*predicateListByFilterForm(form, rootCount, cbCount).toTypedArray())
        return TabrResultQuery(tqData.resultList, entityManager.createQuery(cCount).singleResult)
    }

    private fun predicateListByFilterForm(
        form: ListFilterForm,
        root: Root<CompanyDetail>,
        cb: CriteriaBuilder
    ): List<Predicate> {
        val predicateList = mutableListOf<Predicate>()
        //
        val name = form.name
        if (name.isNotBlank()) predicateList += cb.like(root[CompanyDetailM.NAME], "%$name%")
        //
        val value = form.value
        if (value.isNotBlank()) predicateList += cb.like(root[CompanyDetailM.VALUE], "%$value%")
        //
        val sort = form.sort
        if (sort != null) predicateList += cb.equal(root.get<Int>(CompanyDetailM.SORT), sort)
        return predicateList
    }
}