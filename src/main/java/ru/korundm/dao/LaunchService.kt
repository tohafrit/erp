package ru.korundm.dao

import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.BaseConstant.ONE_LONG
import ru.korundm.entity.Launch
import ru.korundm.entity.User
import ru.korundm.helper.DynamicObject
import ru.korundm.constant.ObjAttr
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.helper.TabrResultQuery.Companion.instance
import ru.korundm.repository.LaunchRepository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.*

interface LaunchService : CommonService<Launch> {

    fun findAllSortedYearNumberDesc(launchId: Long? = null): List<Launch>
    fun findTableData(tableInput: TabrIn, form: DynamicObject, launchId: Long? = null): TabrResultQuery<Launch>
    fun getLastNumber(year: Int, launchId: Long? = null): Int
    fun findLastLaunch(launchId: Long? = null): Launch?
    fun findNextLaunch(year: Int, number: Int, launchId: Long? = null): Launch?
    fun findPrevLaunch(year: Int, number: Int, launchId: Long? = null): Launch?
    fun hasLaunches(launchId: Long): Boolean
    fun findAllByApprovalDateIsNull(): List<Launch>
}

@Service
@Transactional
class LaunchServiceImpl(
    private val repository: LaunchRepository
) : LaunchService {

    private val cl = Launch::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<Launch> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<Launch> = repository.findAllById(idList)

    override fun save(obj: Launch): Launch {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<Launch>): List<Launch> = repository.saveAll(objectList)

    override fun read(id: Long): Launch? = repository.findById(id).orElse(null)

    override fun delete(obj: Launch) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findAllSortedYearNumberDesc(launchId: Long?) = repository.findAllByLaunchIdOrderByYearDescNumberDesc(launchId)

    override fun findAllByApprovalDateIsNull() = repository.findAllByApprovalDateIsNull()

    override fun findTableData(tableInput: TabrIn, form: DynamicObject, launchId: Long?): TabrResultQuery<Launch> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        select.where(if (launchId == null) {
            val subQuery = cq.subquery(Long::class.java)
            val subRoot = subQuery.from(cl)
            subQuery.select(cb.literal(ONE_LONG)).where(
                cb.equal(subRoot.get<Launch>(Launch::launch.name), root.get<Long>(Launch::id.name)),
                *filterPredicateList(subRoot, cb, form)
            )
            cb.or(cb.exists(subQuery), cb.and(*filterPredicateList(root, cb, form), root.get<Launch>(Launch::launch.name).isNull))
        } else {
            cb.and(
                cb.equal(root.get<Launch>(Launch::launch.name), launchId),
                *filterPredicateList(root, cb, form)
            )
        })
        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                Launch::numberInYear.name -> {
                    prSort(root.get<Int>(Launch::year.name))
                    prSort(root.get<Int>(Launch::number.name))
                }
                Launch::approvalDate.name -> prSort(root.get<LocalDate>(Launch::approvalDate.name))
                Launch::approvedBy.name -> prSort(root.get<User>(Launch::approvedBy.name))
                Launch::comment.name -> prSort(root.get<String>(Launch::comment.name))
                else -> prSort(root.get<Long>(Launch::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select)
        typedQuery.firstResult = tableInput.start
        typedQuery.maxResults = tableInput.size
        return instance(typedQuery.resultList)
    }

    private fun filterPredicateList(root: Root<Launch>, cb: CriteriaBuilder, form: DynamicObject): Array<Predicate> {
        val predicateList = mutableListOf<Predicate>()
        val pathApprovalDate = root.get<LocalDate>(Launch::approvalDate.name)
        form.long(ObjAttr.APPROVED_BY)?.let { predicateList += cb.equal(root.get<User>(Launch::approvedBy.name), it) }
        form.date(ObjAttr.APPROVAL_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathApprovalDate, it) }
        form.date(ObjAttr.APPROVAL_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathApprovalDate, it) }
        return predicateList.toTypedArray()
    }

    override fun getLastNumber(year: Int, launchId: Long?): Int {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(Int::class.java)
        val root = cq.from(cl)
        val numberPath = root.get<Int>(Launch::number.name)
        val launchPath = root.get<Launch>(Launch::launch.name)
        return em.createQuery(cq.select(numberPath).where(
            launchId?.let { cb.equal(launchPath, launchId) } ?: launchPath.isNull,
            cb.equal(root.get<Int>(Launch::year.name), year)
        ).orderBy(cb.desc(numberPath))).setMaxResults(ONE_INT).resultList.singleOrNull()?.inc() ?: ONE_INT
    }

    override fun findLastLaunch(launchId: Long?): Launch? {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val launchPath = root.get<Launch>(Launch::launch.name)
        return em.createQuery(cq.select(root).where(
            launchId?.let { cb.equal(launchPath, launchId) } ?: launchPath.isNull
        ).orderBy(cb.desc(root.get<Int>(Launch::year.name)), cb.desc(root.get<Int>(Launch::number.name))))
            .setMaxResults(ONE_INT).resultList.singleOrNull()
    }

    override fun findNextLaunch(year: Int, number: Int, launchId: Long?): Launch? = findNextOrPrev(year, number, true, launchId)

    override fun findPrevLaunch(year: Int, number: Int, launchId: Long?): Launch? = findNextOrPrev(year, number, false, launchId)

    private fun findNextOrPrev(year: Int, number: Int, isNext: Boolean, launchId: Long?): Launch? {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val yearPath = root.get<Int>(Launch::year.name)
        val numberPath = root.get<Int>(Launch::number.name)
        val launchPath = root.get<Launch>(Launch::launch.name)
        val fnCompare = { expr: Expression<out Number>, value: Int -> if (isNext) cb.gt(expr, value) else cb.lt(expr, value) }
        val fnSort = { expr: Expression<*> -> if (isNext) cb.asc(expr) else cb.desc(expr) }
        return em.createQuery(cq.select(root).where(
            launchId?.let { cb.equal(launchPath, launchId) } ?: launchPath.isNull,
            cb.or(fnCompare(yearPath, year), cb.and(cb.equal(yearPath, year), fnCompare(numberPath, number)))
        ).orderBy(fnSort(yearPath), fnSort(numberPath))).setMaxResults(ONE_INT).resultList.singleOrNull()
    }

    override fun hasLaunches(launchId: Long) = repository.existsByLaunchId(launchId)
}