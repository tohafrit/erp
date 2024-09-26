package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.*
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.FunctionalityRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.*

interface FunctionalityService : CommonService<Functionality> {

    fun getByCode(entityId: Long, code: String)

    fun findTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<Functionality>

    fun getAllByEntity(entity: TechnologicalEntity): List<Functionality>

    fun removeAllByParams(entity: TechnologicalEntity, functionality: Functionality?, code: String)

    fun getAllByEntity(entityId: Long): List<Functionality>

    fun getAllByCode(entityId: Long, code: String): List<Functionality>

    fun exists(parentId: Long, code: String): Boolean
}

@Service
@Transactional
class FunctionalityServiceImpl(
    private val repository: FunctionalityRepository
) : FunctionalityService {

    private val cl = Functionality::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<Functionality> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<Functionality> = repository.findAllById(idList)

    override fun save(obj: Functionality) = repository.save(obj)

    override fun saveAll(objectList: List<Functionality>): List<Functionality> = repository.saveAll(objectList)

    override fun read(id: Long): Functionality? = repository.findById(id).orElse(null)

    override fun delete(obj: Functionality) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<Functionality> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.ENTITY_NUMBER -> prSort(root.get<String>(TechnologicalEntity::entityNumber.name))
                else -> prSort(root.get<Long>(TechnologicalEntity::id.name))
            }
            cq.orderBy(orderList)
        }
        val predicateList = mutableListOf<Predicate>()

        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()).distinct(true))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun getByCode(entityId: Long, code: String) = repository.getFirstByTechnologicalEntity_IdAndServiceSymbol_Code(entityId, code)

    override fun getAllByEntity(entity: TechnologicalEntity): List<Functionality> = repository.findAllByTechnologicalEntity(entity)

    override fun removeAllByParams(entity: TechnologicalEntity, functionality: Functionality?, code: String) = repository.removeAllByTechnologicalEntityAndParentAndServiceSymbol_Code(entity, functionality, code)

    override fun getAllByEntity(entityId: Long): List<Functionality> = repository.findAllByTechnologicalEntity_IdAndParentIsNullOrderBySort(entityId)

    override fun getAllByCode(entityId: Long, code: String): List<Functionality> = repository.findAllByTechnologicalEntity_IdAndServiceSymbol_CodeOrderBySortDesc(entityId, code)

    override fun exists(parentId: Long, code: String): Boolean = repository.existsByParent_IdAndServiceSymbol_Code(parentId, code)
}
