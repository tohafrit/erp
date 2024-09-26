package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.ProductionArea
import ru.korundm.entity.TechnologicalTool
import ru.korundm.enumeration.TechnologicalToolType
import ru.korundm.form.search.TechnologicalToolFilterForm
import ru.korundm.repository.TechnologicalToolRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate

interface TechnologicalToolService : CommonService<TechnologicalTool> {

    fun getAllByTypeAndProductionArea(
        type: TechnologicalToolType,
        productionAreaList: List<ProductionArea>
    ): List<TechnologicalTool>
    fun getAllByForm(form: TechnologicalToolFilterForm): List<TechnologicalTool>
    fun findTableData(toolIdList: List<Long>): List<TechnologicalTool>
}

@Service
@Transactional
class TechnologicalToolServiceImpl(
    private val repository: TechnologicalToolRepository
) : TechnologicalToolService {

    private val cl = TechnologicalTool::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<TechnologicalTool> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<TechnologicalTool> = repository.findAllById(idList)

    override fun save(obj: TechnologicalTool): TechnologicalTool {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<TechnologicalTool>): List<TechnologicalTool> = repository.saveAll(objectList)

    override fun read(id: Long): TechnologicalTool? = repository.findById(id).orElse(null)

    override fun delete(obj: TechnologicalTool) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun getAllByTypeAndProductionArea(
        type: TechnologicalToolType,
        productionAreaList: List<ProductionArea>
    ) = repository.findAllByTypeAndProductionAreaListIn(type, productionAreaList)

    override fun getAllByForm(form: TechnologicalToolFilterForm): List<TechnologicalTool> {
        val cb: CriteriaBuilder = em.criteriaBuilder
        val criteria = cb.createQuery(TechnologicalTool::class.java)
        val root = criteria.from(TechnologicalTool::class.java)
        val predicateList = mutableListOf<Predicate>()
        // Наименование
        if (form.name.isNotBlank()) {
            predicateList.add(cb.like(cb.lower(root.get(ObjAttr.NAME)), "%" + form.name + "%"))
        }
        // Тип
        form.type?.let {
            predicateList.add(cb.equal(root.get<Long>(ObjAttr.TYPE), it.id))
        }
        // Участки
        /*if (form.productionAreaIdList.isNotEmpty()) {
            val productionAreaJoin: Join<TechnologicalTool, ProductionArea> =
                root.join(ObjAttr.PRODUCTION_AREA_LIST, JoinType.INNER)
            predicateList.add(productionAreaJoin.get(ProductionArea_.id).`in`(form.productionAreaIdList))
        }*/
        criteria.select(root).where(*predicateList.toTypedArray())
        return em.createQuery(criteria).resultList
    }

    override fun findTableData(toolIdList: List<Long>): List<TechnologicalTool> {
        val cb = em.criteriaBuilder
        val c = cb.createQuery(cl)
        val root = c.from(cl)
        val predicateList = mutableListOf<Predicate>()
        if (toolIdList.isNotEmpty()) predicateList += cb.not(root.get<Long>(ObjAttr.ID).`in`(toolIdList))
        val select = c.select(root).where(*predicateList.toTypedArray())
        return em.createQuery(select).resultList
    }
}