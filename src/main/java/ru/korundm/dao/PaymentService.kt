package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ContractSection
import ru.korundm.entity.Payment
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.PaymentRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface PaymentService : CommonService<Payment> {

    fun getBySectionId(id: Long): List<Payment>
    fun findTableData(input: TabrIn, sectionId: Long): TabrResultQuery<Payment>
    fun findByInvoiceId(id: Long): List<Payment>
}

@Service
@Transactional
class PaymentServiceImpl(
    private val paymentRepository: PaymentRepository
) : PaymentService {

    private val cl = Payment::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<Payment> = paymentRepository.findAll()

    override fun getAllById(idList: List<Long>): List<Payment> = paymentRepository.findAllById(idList)

    override fun save(obj: Payment) = paymentRepository.save(obj)

    override fun saveAll(objectList: List<Payment>): List<Payment> = paymentRepository.saveAll(objectList)

    override fun read(id: Long): Payment? = paymentRepository.findById(id).orElse(null)

    override fun delete(obj: Payment) = paymentRepository.delete(obj)

    override fun deleteById(id: Long) = paymentRepository.deleteById(id)

    override fun getBySectionId(id: Long) = paymentRepository.findAllByContractSectionId(id)

    override fun findByInvoiceId(id: Long) = paymentRepository.findAllByInvoiceId(id)

    override fun findTableData(input: TabrIn, sectionId: Long): TabrResultQuery<Payment> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        select.where(cb.equal(root.get<ContractSection>(Payment::contractSection.name), sectionId))

        val typedQuery = em.createQuery(select)
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        val resultList = typedQuery.resultList
        return TabrResultQuery.instance(resultList)
    }
}