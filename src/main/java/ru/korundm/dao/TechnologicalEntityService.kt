package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.*
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.TechnologicalEntityRepository
import java.time.LocalDateTime
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.*

interface TechnologicalEntityService : CommonService<TechnologicalEntity> {

    fun findTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<TechnologicalEntity>

    fun existsByEntityType(entityTypeId: Long): Boolean
}

@Service
@Transactional
class TechnologicalEntityServiceImpl(
    private val repository: TechnologicalEntityRepository
) : TechnologicalEntityService {

    private val cl = TechnologicalEntity::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<TechnologicalEntity> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<TechnologicalEntity> = repository.findAllById(idList)

    override fun save(obj: TechnologicalEntity) = repository.save(obj)

    override fun saveAll(objectList: List<TechnologicalEntity>): List<TechnologicalEntity> = repository.saveAll(objectList)

    override fun read(id: Long): TechnologicalEntity? = repository.findById(id).orElse(null)

    override fun delete(obj: TechnologicalEntity) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<TechnologicalEntity> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        val reconciliationJoin = root.join<TechnologicalEntity, TechnologicalEntityReconciliation>(TechnologicalEntity::technologicalEntityReconciliation.name, JoinType.LEFT)
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.ENTITY_NUMBER -> prSort(root.get<String>(TechnologicalEntity::entityNumber.name))
                ObjAttr.SET_NUMBER -> prSort(root.get<String>(TechnologicalEntity::setNumber.name))
                ObjAttr.APPROVED -> prSort(reconciliationJoin.get<Boolean>(TechnologicalEntityReconciliation::approved.name))
                ObjAttr.DESIGNED -> prSort(reconciliationJoin.get<Boolean>(TechnologicalEntityReconciliation::designed.name))
                ObjAttr.CHECKED -> prSort(reconciliationJoin.get<Boolean>(TechnologicalEntityReconciliation::checked.name))
                ObjAttr.METROLOGIST -> prSort(reconciliationJoin.get<Boolean>(TechnologicalEntityReconciliation::metrologist.name))
                ObjAttr.NORMOCONTROLLER -> prSort(reconciliationJoin.get<Boolean>(TechnologicalEntityReconciliation::normocontroller.name))
                ObjAttr.MILITARY -> prSort(reconciliationJoin.get<Boolean>(TechnologicalEntityReconciliation::military.name))
                ObjAttr.TECHNOLOGICAL_CHIEF -> prSort(reconciliationJoin.get<Boolean>(TechnologicalEntityReconciliation::technologicalChief.name))
                else -> prSort(root.get<Long>(TechnologicalEntity::id.name))
            }
            cq.orderBy(orderList)
        }
        val predicateList = mutableListOf<Predicate>()
        val entityNumber = form.string(ObjAttr.ENTITY_NUMBER)
        if (!entityNumber.isNullOrEmpty()) predicateList += cb.like(root.get(TechnologicalEntity::entityNumber.name), "%$entityNumber%")
        val setNumber = form.string(ObjAttr.SET_NUMBER)
        if (!setNumber.isNullOrEmpty()) predicateList += cb.like(root.get(TechnologicalEntity::setNumber.name), "%$setNumber%")
        val entityTypeId = form.long(ObjAttr.ENTITY_TYPE_ID)
        entityTypeId?.let {
            val entityTypeJoin = root.join<TechnologicalEntity, TechnologicalEntityType>(TechnologicalEntity::entityType.name, JoinType.LEFT)
            predicateList += cb.equal(entityTypeJoin.get<Long>(TechnologicalEntityType::id.name), entityTypeId)
        }
        val conditionalName = form.string(ObjAttr.CONDITIONAL_NAME)
        conditionalName?.let {
            val appJoin = root.join<TechnologicalEntity, TechnologicalEntityApplicability>(TechnologicalEntity::technologicalEntityApplicabilityList.name, JoinType.LEFT)
            val productJoin = appJoin.join<TechnologicalEntityApplicability, Product>(TechnologicalEntityApplicability::product.name, JoinType.LEFT)
            predicateList += cb.like(productJoin.get(Product::conditionalName.name), "%$conditionalName%")
        }

        val approvedById = form.long(ObjAttr.APPROVED_BY_ID)
        val approvedOnFrom = form.dateTime(ObjAttr.APPROVED_ON_FROM)
        val approvedOnTo = form.dateTime(ObjAttr.APPROVED_ON_TO)

        val designedById = form.long(ObjAttr.DESIGNED_BY_ID)
        val designedOnFrom = form.dateTime(ObjAttr.DESIGNED_ON_FROM)
        val designedOnTo = form.dateTime(ObjAttr.DESIGNED_ON_TO)

        val checkedById = form.long(ObjAttr.CHECKED_BY_ID)
        val checkedOnFrom = form.dateTime(ObjAttr.CHECKED_ON_FROM)
        val checkedOnTo = form.dateTime(ObjAttr.CHECKED_ON_TO)

        val militaryById = form.long(ObjAttr.MILITARY_BY_ID)
        val militaryOnFrom = form.dateTime(ObjAttr.MILITARY_ON_FROM)
        val militaryOnTo = form.dateTime(ObjAttr.MILITARY_ON_TO)

        val metrologistById = form.long(ObjAttr.METROLOGIST_BY_ID)
        val metrologistOnFrom = form.dateTime(ObjAttr.METROLOGIST_ON_FROM)
        val metrologistOnTo = form.dateTime(ObjAttr.METROLOGIST_ON_TO)

        val normocontrollerById = form.long(ObjAttr.NORMOCONTROLLER_BY_ID)
        val normocontrollerOnFrom = form.dateTime(ObjAttr.NORMOCONTROLLER_ON_FROM)
        val normocontrollerOnTo = form.dateTime(ObjAttr.NORMOCONTROLLER_ON_TO)

        val technologicalChiefById = form.long(ObjAttr.TECHNOLOGICAL_CHIEF_BY_ID)
        val technologicalChiefOnFrom = form.dateTime(ObjAttr.TECHNOLOGICAL_CHIEF_ON_FROM)
        val technologicalChiefOnTo = form.dateTime(ObjAttr.TECHNOLOGICAL_CHIEF_ON_TO)

        approvedById?.let {
            predicateList += cb.equal(reconciliationJoin.get<Long>(ObjAttr.APPROVED_BY), it)
        }
        val approvedOn = reconciliationJoin.get<LocalDateTime>(ObjAttr.APPROVED_ON)
        approvedOnFrom?.let {
            predicateList += cb.greaterThanOrEqualTo(approvedOn, it)
        }
        approvedOnTo?.let {
            predicateList += cb.lessThanOrEqualTo(approvedOn, it)
        }

        designedById?.let {
            predicateList += cb.equal(reconciliationJoin.get<Long>(ObjAttr.DESIGNED_BY), it)
        }
        val designedOn = reconciliationJoin.get<LocalDateTime>(ObjAttr.DESIGNED_ON)
        designedOnFrom?.let {
            predicateList += cb.greaterThanOrEqualTo(designedOn, it)
        }
        designedOnTo?.let {
            predicateList += cb.lessThanOrEqualTo(designedOn, it)
        }

        checkedById?.let {
            predicateList += cb.equal(reconciliationJoin.get<Long>(ObjAttr.CHECKED_BY), it)
        }
        val checkedOn = reconciliationJoin.get<LocalDateTime>(ObjAttr.CHECKED_ON)
        checkedOnFrom?.let {
            predicateList += cb.greaterThanOrEqualTo(checkedOn, it)
        }
        checkedOnTo?.let {
            predicateList += cb.lessThanOrEqualTo(checkedOn, it)
        }

        militaryById?.let {
            predicateList += cb.equal(reconciliationJoin.get<Long>(ObjAttr.MILITARY_BY), it)
        }
        val militaryOn = reconciliationJoin.get<LocalDateTime>(ObjAttr.MILITARY_ON)
        militaryOnFrom?.let {
            predicateList += cb.greaterThanOrEqualTo(militaryOn, it)
        }
        militaryOnTo?.let {
            predicateList += cb.lessThanOrEqualTo(militaryOn, it)
        }

        metrologistById?.let {
            predicateList += cb.equal(reconciliationJoin.get<Long>(ObjAttr.METROLOGIST_BY), it)
        }
        val metrologistOn = reconciliationJoin.get<LocalDateTime>(ObjAttr.METROLOGIST_ON)
        metrologistOnFrom?.let {
            predicateList += cb.greaterThanOrEqualTo(metrologistOn, it)
        }
        metrologistOnTo?.let {
            predicateList += cb.lessThanOrEqualTo(metrologistOn, it)
        }

        normocontrollerById?.let {
            predicateList += cb.equal(reconciliationJoin.get<Long>(ObjAttr.NORMOCONTROLLER_BY), it)
        }
        val normocontrollerOn = reconciliationJoin.get<LocalDateTime>(ObjAttr.NORMOCONTROLLER_ON)
        normocontrollerOnFrom?.let {
            predicateList += cb.greaterThanOrEqualTo(normocontrollerOn, it)
        }
        normocontrollerOnTo?.let {
            predicateList += cb.lessThanOrEqualTo(normocontrollerOn, it)
        }

        technologicalChiefById?.let {
            predicateList += cb.equal(reconciliationJoin.get<Long>(ObjAttr.TECHNOLOGICAL_CHIEF), it)
        }
        val technologicalChiefOn = reconciliationJoin.get<LocalDateTime>(ObjAttr.TECHNOLOGICAL_CHIEF_ON)
        technologicalChiefOnFrom?.let {
            predicateList += cb.greaterThanOrEqualTo(technologicalChiefOn, it)
        }
        technologicalChiefOnTo?.let {
            predicateList += cb.lessThanOrEqualTo(technologicalChiefOn, it)
        }

        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()).distinct(true))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun existsByEntityType(entityTypeId: Long): Boolean = repository.existsByEntityType_Id(entityTypeId)
}
