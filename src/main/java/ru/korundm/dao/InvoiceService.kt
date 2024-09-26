package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ContractSection
import ru.korundm.entity.Invoice
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.InvoiceRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface InvoiceService : CommonService<Invoice> {

    fun findTableData(input: TabrIn, sectionId: Long): TabrResultQuery<Invoice>
}

@Service
@Transactional
class InvoiceServiceImpl(
    private val repository: InvoiceRepository
) : InvoiceService {

    private val cl = Invoice::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<Invoice> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<Invoice> = repository.findAllById(idList)

    override fun save(obj: Invoice): Invoice {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<Invoice>): List<Invoice> = repository.saveAll(objectList)

    override fun read(id: Long): Invoice? = repository.findById(id).orElse(null)

    override fun delete(obj: Invoice) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(input: TabrIn, sectionId: Long): TabrResultQuery<Invoice> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        select.where(cb.equal(root.get<ContractSection>(Invoice::contractSection.name), sectionId))
        val typedQuery = em.createQuery(select)
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}