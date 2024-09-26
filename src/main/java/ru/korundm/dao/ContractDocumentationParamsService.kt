package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.ContractDocumentationParam
import ru.korundm.entity.ContractSection
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.ContractDocumentationParamsRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path

interface ContractDocumentationParamsService : CommonService<ContractDocumentationParam> {

    fun findTableData(input: TabrIn, sectionId: Long): TabrResultQuery<ContractDocumentationParam>
}

@Service
@Transactional
class ContractDocumentationParamsServiceImpl(
    private val repository: ContractDocumentationParamsRepository
) : ContractDocumentationParamsService {

    private val cl = ContractDocumentationParam::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ContractDocumentationParam> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ContractDocumentationParam> = repository.findAllById(idList)

    override fun save(obj: ContractDocumentationParam): ContractDocumentationParam {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ContractDocumentationParam>): List<ContractDocumentationParam> = repository.saveAll(objectList)

    override fun read(id: Long): ContractDocumentationParam? = repository.findById(id).orElse(null)

    override fun delete(obj: ContractDocumentationParam) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(input: TabrIn, sectionId: Long): TabrResultQuery<ContractDocumentationParam> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        select.where(cb.equal(root.get<ContractSection>(ContractDocumentationParam::contractSection.name), sectionId))
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.NAME -> prSort(root.get<String>(ContractDocumentationParam::name.name))
                else -> prSort(root.get<Long>(ContractDocumentationParam::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select)
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}