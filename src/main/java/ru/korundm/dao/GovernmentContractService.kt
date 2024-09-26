package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.GovernmentContract
import ru.korundm.entity.ServiceType
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.GovernmentContractRepository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface GovernmentContractService : CommonService<GovernmentContract> {

    fun findTableData(tableInput: TabrIn, form: DynamicObject): TabrResultQuery<GovernmentContract>
    fun getGovernmentContractByIdentifier(identifier: String): GovernmentContract?
}

@Service
@Transactional
class GovernmentContractServiceImp(
    private val governmentContractRepository: GovernmentContractRepository
) : GovernmentContractService {

    private val cl = GovernmentContract::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<GovernmentContract> = governmentContractRepository.findAll()

    override fun getAllById(idList: List<Long>): List<GovernmentContract> = governmentContractRepository.findAllById(idList)

    override fun save(obj: GovernmentContract) = governmentContractRepository.save(obj)

    override fun saveAll(objectList: List<GovernmentContract>): List<GovernmentContract> = governmentContractRepository.saveAll(objectList)

    override fun read(id: Long): GovernmentContract? = governmentContractRepository.findById(id).orElse(null)

    override fun delete(obj: GovernmentContract) = governmentContractRepository.delete(obj)

    override fun deleteById(id: Long) = governmentContractRepository.deleteById(id)

    override fun getGovernmentContractByIdentifier(identifier: String) = governmentContractRepository.findFirstByIdentifier(identifier)

    override fun findTableData(
        tableInput: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<GovernmentContract> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        val predicateList = mutableListOf<Predicate>()

        val identifier = form.stringNotNull(ObjAttr.IDENTIFIER)
        if (identifier.isNotBlank()) {
            predicateList += cb.like(root.get(GovernmentContract::identifier.name), "%$identifier%")
        }

        val date = root.get<LocalDate>(GovernmentContract::date.name)
        form.date(ObjAttr.DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(date, it) }
        form.date(ObjAttr.DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(date, it) }

        val comment = form.stringNotNull(ObjAttr.COMMENT)
        if (comment.isNotBlank()) {
            predicateList += cb.like(root.get(ServiceType::comment.name), "%$comment%")
        }

        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                GovernmentContract::identifier.name -> prSort(root.get<String>(GovernmentContract::identifier.name))
                GovernmentContract::date.name -> prSort(root.get<LocalDate>(GovernmentContract::date.name))
                GovernmentContract::comment.name -> prSort(root.get<String>(GovernmentContract::comment.name))
                else -> prSort(root.get<Long>(GovernmentContract::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = tableInput.start
        typedQuery.maxResults = tableInput.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}