package ru.korundm.dao

import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.BaseConstant.ONE_LONG
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.*
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.LaunchNoteRepository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.*

interface LaunchNoteService : CommonService<LaunchNote> {

    fun getLastNumber(year: Int): Int
    fun findListTableData(tableData: TabrIn, form: DynamicObject): TabrResultQuery<LaunchNote>
}

@Service
@Transactional
class LaunchNoteServiceImpl(
    private val repository: LaunchNoteRepository
) : LaunchNoteService {

    private val cl = LaunchNote::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<LaunchNote> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<LaunchNote> = repository.findAllById(idList)

    override fun save(obj: LaunchNote): LaunchNote {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<LaunchNote>): List<LaunchNote> = repository.saveAll(objectList)

    override fun read(id: Long): LaunchNote? = repository.findById(id).orElse(null)

    override fun delete(obj: LaunchNote) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun getLastNumber(year: Int): Int {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(Int::class.java)
        val root = cq.from(cl)
        val numberPath = root.get<Int>(LaunchNote::number.name)
        return em.createQuery(cq.select(numberPath).where(
            cb.equal(root.get<Int>(LaunchNote::year.name), year)
        ).orderBy(cb.desc(numberPath))).setMaxResults(ONE_INT).resultList.singleOrNull()?.inc() ?: ONE_INT
    }

    override fun findListTableData(
        tableData: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<LaunchNote> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        select.where(*filterPredicateList(root, cb, cq, form))
        tableData.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                LaunchNote::numberInYear.name -> {
                    prSort(root.get<Int>(LaunchNote::year.name))
                    prSort(root.get<Int>(LaunchNote::number.name))
                }
                LaunchNote::createDate.name -> prSort(root.get<LocalDate>(LaunchNote::createDate.name))
                LaunchNote::createdBy.name -> prSort(root.get<User>(LaunchNote::createdBy.name))
                LaunchNote::agreementDate.name -> prSort(root.get<LocalDate>(LaunchNote::agreementDate.name))
                LaunchNote::agreedBy.name -> prSort(root.get<User>(LaunchNote::agreedBy.name))
                LaunchNote::comment.name -> prSort(root.get<String>(LaunchNote::comment.name))
                else -> prSort(root.get<Long>(LaunchNote::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select)
        typedQuery.firstResult = tableData.start
        typedQuery.maxResults = tableData.size
        val resultList = typedQuery.resultList
        return TabrResultQuery.instance(resultList)
    }

    private fun filterPredicateList(
        root: Root<LaunchNote>,
        cb: CriteriaBuilder,
        cq: CriteriaQuery<LaunchNote>,
        form: DynamicObject
    ): Array<Predicate> {
        val predicateList = mutableListOf<Predicate>()
        val productName = form.stringNotNull(ObjAttr.PRODUCT_NAME)
        if (productName.isNotBlank()) {
            val subQuery = cq.subquery(Long::class.java)
            val subRoot = subQuery.from(LaunchNoteProduct::class.java)
            subQuery.select(cb.literal(ONE_LONG)).where(
                cb.equal(subRoot.get<LaunchNote>(LaunchNoteProduct::note.name), root.get<Long>(LaunchNote::id.name)),
                cb.like(subRoot.get<LaunchProduct>(LaunchNoteProduct::launchProduct.name)
                    .get<Product>(LaunchProduct::product.name).get(Product::conditionalName.name), "%$productName%")
            )
            predicateList += cb.exists(subQuery)
        }
        //
        form.long(ObjAttr.CREATED_BY)?.let { predicateList += cb.equal(root.get<User>(LaunchNote::createdBy.name), it) }
        form.long(ObjAttr.AGREED_BY)?.let { predicateList += cb.equal(root.get<User>(LaunchNote::agreedBy.name), it) }
        val pathCreateDate = root.get<LocalDate>(LaunchNote::createDate.name)
        form.date(ObjAttr.CREATE_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathCreateDate, it) }
        form.date(ObjAttr.CREATE_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathCreateDate, it) }
        val pathAgreementDate = root.get<LocalDate>(LaunchNote::agreementDate.name)
        form.date(ObjAttr.AGREEMENT_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathAgreementDate, it) }
        form.date(ObjAttr.AGREEMENT_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathAgreementDate, it) }
        return predicateList.toTypedArray()
    }
}