package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.DocumentLabel
import ru.korundm.entity.ProductionShipmentLetter
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.DocumentLabelRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface DocumentLabelService : CommonService<DocumentLabel> {

    fun findTableData(tableInput: TabrIn, form: DynamicObject): TabrResultQuery<DocumentLabel>
    fun getByLabel(label: String): DocumentLabel
    fun getByUserId(userId: Long): DocumentLabel
}

@Service
@Transactional
class DocumentLabelServiceImpl(
    private val documentLabelRepository: DocumentLabelRepository
) : DocumentLabelService {

    private val cl = DocumentLabel::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<DocumentLabel> = documentLabelRepository.findAll()

    override fun getAllById(idList: List<Long>): List<DocumentLabel> = documentLabelRepository.findAllById(idList)

    override fun save(obj: DocumentLabel) = documentLabelRepository.save(obj)

    override fun saveAll(objectList: List<DocumentLabel>): List<DocumentLabel> = documentLabelRepository.saveAll(objectList)

    override fun read(id: Long): DocumentLabel? = documentLabelRepository.findById(id).orElse(null)

    override fun delete(obj: DocumentLabel) = documentLabelRepository.delete(obj)

    override fun deleteById(id: Long) = documentLabelRepository.deleteById(id)

    override fun getByLabel(label: String) = documentLabelRepository.findFirstByLabel(label)

    override fun getByUserId(userId: Long) = documentLabelRepository.findFirstByUserId(userId)

    override fun findTableData(
        tableInput: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<DocumentLabel> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        val predicateList = mutableListOf<Predicate>()

        form.string(ObjAttr.LABEL)?.let { predicateList += cb.like(root.get(DocumentLabel::label.name), "%$it%") }

        form.string(ObjAttr.EMPLOYEE_POSITION)?.let { predicateList += cb.like(root.get(DocumentLabel::employeePosition.name), "%$it%") }

        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.LABEL -> prSort(root.get<String>(DocumentLabel::label.name))
                ObjAttr.EMPLOYEE_POSITION -> prSort(root.get<String>(DocumentLabel::employeePosition.name))
                else -> prSort(root.get<Long>(ProductionShipmentLetter::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = tableInput.start
        typedQuery.maxResults = tableInput.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}