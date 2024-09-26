package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.Launch
import ru.korundm.entity.PurchasePlan
import ru.korundm.entity.User
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.PurchasePlanRepository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface PurchasePlanService : CommonService<PurchasePlan> {

    fun findTableData(input: TabrIn, form: DynamicObject): TabrResultQuery<PurchasePlan>
}

@Service
@Transactional
class PurchasePlanServiceImpl(
    private val repository: PurchasePlanRepository
) : PurchasePlanService {

    private val cl = PurchasePlan::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<PurchasePlan> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<PurchasePlan> = repository.findAllById(idList)

    override fun save(obj: PurchasePlan): PurchasePlan {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<PurchasePlan>): List<PurchasePlan> = repository.saveAll(objectList)

    override fun read(id: Long): PurchasePlan? = repository.findById(id).orElse(null)

    override fun delete(obj: PurchasePlan) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<PurchasePlan> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            val userJoin = root.join<PurchasePlan, User>(PurchasePlan::createdBy.name, JoinType.LEFT)
            val approveUserJoin = root.join<PurchasePlan, User>(PurchasePlan::approvedBy.name, JoinType.LEFT)
            val launchJoin = root.join<PurchasePlan, Launch>(PurchasePlan::launch.name, JoinType.LEFT)
            when (it.field) {
                ObjAttr.NAME -> prSort(root.get<String>(PurchasePlan::name.name))
                ObjAttr.CREATE_DATE -> prSort(root.get<LocalDate>(PurchasePlan::createDate.name))
                ObjAttr.CREATED_BY -> prSort(userJoin.get<String>(ObjAttr.LAST_NAME))
                ObjAttr.NUMBER_IN_YEAR -> {
                    prSort(launchJoin.get<Int>(Launch::year.name))
                    prSort(launchJoin.get<Int>(Launch::number.name))
                }
                ObjAttr.VERSION -> prSort(root.get<Long>(PurchasePlan::bomVersionType.name))
                ObjAttr.DATE -> prSort(root.get<LocalDate>(PurchasePlan::onTheWayLastDate.name))
                ObjAttr.RESERVE -> prSort(root.get<Long>(PurchasePlan::reserveUseType.name))
                ObjAttr.APPROVAL_DATE -> prSort(root.get<LocalDate>(PurchasePlan::approvalDate.name))
                ObjAttr.APPROVED_BY -> prSort(approveUserJoin.get<String>(ObjAttr.LAST_NAME))
                else -> prSort(root.get<Long>(ObjAttr.ID))
            }
            cq.orderBy(orderList)
        }
        val predicateList = mutableListOf<Predicate>()
        // Наименование
        form.string(ObjAttr.NAME)?.let { predicateList += cb.like(root.get(PurchasePlan::name.name), "%$it%") }
        // Дата создания
        val createDate = root.get<LocalDate>(PurchasePlan::createDate.name)
        form.date(ObjAttr.CREATE_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(createDate, it) }
        form.date(ObjAttr.CREATE_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(createDate, it) }
        // Кто создал
        form.long(ObjAttr.CREATED_BY)?.let {
            predicateList += cb.equal(root.get<Long>(PurchasePlan::createdBy.name), it)
        }
        // Запуск
        form.long(ObjAttr.LAUNCH_ID)?.let {
            val launchJoin = root.join<PurchasePlan, Launch>(PurchasePlan::launch.name, JoinType.LEFT)
            predicateList += cb.equal(launchJoin.get<Long>(Launch::id.name), it)
        }
        // Версия ЗС
        form.long(ObjAttr.VERSION_TYPE)?.let {
            predicateList += cb.equal(root.get<Long>(PurchasePlan::bomVersionType.name), it)
        }
        // Дата отсечки
        val onTheWayLastDate = root.get<LocalDate>(PurchasePlan::onTheWayLastDate.name)
        form.date(ObjAttr.ON_THE_WAY_LAST_DATE_ROM)?.let { predicateList += cb.greaterThanOrEqualTo(onTheWayLastDate, it) }
        form.date(ObjAttr.ON_THE_WAY_LAST_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(onTheWayLastDate, it) }
        // Учет запасов
        form.long(ObjAttr.RESERVE_USE_TYPE)?.let {
            predicateList += cb.equal(root.get<Long>(PurchasePlan::reserveUseType.name), it)
        }
        // Дата утверждения
        val approvalDate = root.get<LocalDate>(PurchasePlan::approvalDate.name)
        form.date(ObjAttr.APPROVAL_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(approvalDate, it) }
        form.date(ObjAttr.APPROVAL_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(approvalDate, it) }
        // Кто утвердил
        form.long(ObjAttr.APPROVED_BY)?.let {
            predicateList += cb.equal(root.get<Long>(PurchasePlan::approvedBy.name), it)
        }

        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}