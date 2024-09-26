package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.Bank
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.BankRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface BankService : CommonService<Bank> {

    fun findTableData(tableInput: TabrIn, form: DynamicObject): TabrResultQuery<Bank>
}

@Service
@Transactional
class BankServiceImpl(
    private val bankRepository: BankRepository
) : BankService {

    private val cl = Bank::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<Bank> = bankRepository.findAll()

    override fun getAllById(idList: List<Long>): List<Bank> = bankRepository.findAllById(idList)

    override fun save(obj: Bank) = bankRepository.save(obj)

    override fun saveAll(objectList: List<Bank>): List<Bank> = bankRepository.saveAll(objectList)

    override fun read(id: Long): Bank? = bankRepository.findById(id).orElse(null)

    override fun delete(obj: Bank) = bankRepository.delete(obj)

    override fun deleteById(id: Long) = bankRepository.deleteById(id)

    override fun findTableData(
        tableInput: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<Bank> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        val predicateList = mutableListOf<Predicate>()

        form.string(ObjAttr.NAME)?.let { predicateList += cb.like(root.get(Bank::name.name), "%$it%") }

        form.string(ObjAttr.LOCATION)?.let { predicateList += cb.like(root.get(Bank::location.name), "%$it%") }

        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.NAME -> prSort(root.get<String>(Bank::name.name))
                ObjAttr.LOCATION -> prSort(root.get<String>(Bank::location.name))
                else -> prSort(root.get<Long>(Bank::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = tableInput.start
        typedQuery.maxResults = tableInput.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}