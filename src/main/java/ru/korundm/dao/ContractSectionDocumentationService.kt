package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.ContractSection
import ru.korundm.entity.ContractSectionDocumentation
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.ContractSectionDocumentationRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path

interface ContractSectionDocumentationService : CommonService<ContractSectionDocumentation> {
    fun findTableData(input: TabrIn, sectionId: Long): TabrResultQuery<ContractSectionDocumentation>
}

@Service
@Transactional
class ContractSectionDocumentationServiceImpl(
    private val repository: ContractSectionDocumentationRepository
) : ContractSectionDocumentationService {

    private val cl = ContractSectionDocumentation::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ContractSectionDocumentation> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ContractSectionDocumentation> = repository.findAllById(idList)

    override fun save(obj: ContractSectionDocumentation): ContractSectionDocumentation {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ContractSectionDocumentation>): List<ContractSectionDocumentation> = repository.saveAll(objectList)

    override fun read(id: Long): ContractSectionDocumentation? = repository.findById(id).orElse(null)

    override fun delete(obj: ContractSectionDocumentation) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(input: TabrIn, sectionId: Long): TabrResultQuery<ContractSectionDocumentation> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        select.where(cb.equal(root.get<ContractSection>(ContractSectionDocumentation::section.name), sectionId))
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.NAME -> prSort(root.get<String>(ContractSectionDocumentation::name.name))
                ObjAttr.COMMENT -> prSort(root.get<String>(ContractSectionDocumentation::comment.name))
                else -> prSort(root.get<Long>(ContractSectionDocumentation::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select)
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        val resultList = typedQuery.resultList
        return TabrResultQuery.instance(resultList)
    }
}