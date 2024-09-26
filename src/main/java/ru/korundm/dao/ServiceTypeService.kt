package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.Contract
import ru.korundm.entity.ServiceType
import ru.korundm.helper.DynamicObject
import ru.korundm.constant.ObjAttr
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.ServiceTypeRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface ServiceTypeService : CommonService<ServiceType> {

    fun findTableData(tableInput: TabrIn, form: DynamicObject): TabrResultQuery<ServiceType>
}

@Service
@Transactional
class ServiceTypeServiceImpl(
    private val serviceTypeRepository: ServiceTypeRepository
) : ServiceTypeService {

    private val cl = ServiceType::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ServiceType> = serviceTypeRepository.findAll()

    override fun getAllById(idList: List<Long>): List<ServiceType> = serviceTypeRepository.findAllById(idList)

    override fun save(obj: ServiceType) = serviceTypeRepository.save(obj)

    override fun saveAll(objectList: List<ServiceType>): List<ServiceType> = serviceTypeRepository.saveAll(objectList)

    override fun read(id: Long): ServiceType? = serviceTypeRepository.findById(id).orElse(null)

    override fun delete(obj: ServiceType) = serviceTypeRepository.delete(obj)

    override fun deleteById(id: Long) = serviceTypeRepository.deleteById(id)

    override fun findTableData(
        tableInput: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<ServiceType> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        val predicateList = mutableListOf<Predicate>()

        val name = form.stringNotNull(ObjAttr.NAME)
        if (name.isNotBlank()) {
            predicateList += cb.like(root.get(ServiceType::name.name), "%$name%")
        }

        val prefix = form.stringNotNull(ObjAttr.PREFIX)
        if (prefix.isNotBlank()) {
            predicateList += cb.like(root.get(ServiceType::prefix.name), "%$prefix%")
        }

        val comment = form.stringNotNull(ObjAttr.COMMENT)
        if (comment.isNotBlank()) {
            predicateList += cb.like(root.get(ServiceType::comment.name), "%$comment%")
        }

        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ServiceType::name.name -> prSort(root.get<String>(ServiceType::name.name))
                ServiceType::prefix.name -> prSort(root.get<String>(ServiceType::prefix.name))
                ServiceType::comment.name -> prSort(root.get<String>(ServiceType::comment.name))
                else -> prSort(root.get<Long>(Contract::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = tableInput.start
        typedQuery.maxResults = tableInput.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}