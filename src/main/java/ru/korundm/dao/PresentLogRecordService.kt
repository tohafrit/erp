package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.*
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.PresentLogRecordRepository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface PresentLogRecordService : CommonService<PresentLogRecord> {

    fun findTableData(tableInput: TabrIn, form: DynamicObject): TabrResultQuery<PresentLogRecord>
    fun findLastLogRecord(): PresentLogRecord?
}

@Service
@Transactional
class PresentLogRecordServiceImpl(
    private val repository: PresentLogRecordRepository
) : PresentLogRecordService {

    private val cl = PresentLogRecord::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<PresentLogRecord> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<PresentLogRecord> = repository.findAllById(idList)

    override fun save(obj: PresentLogRecord): PresentLogRecord {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<PresentLogRecord>): List<PresentLogRecord> = repository.saveAll(objectList)

    override fun read(id: Long): PresentLogRecord? = repository.findById(id).orElse(null)

    override fun delete(obj: PresentLogRecord) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(
        tableInput: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<PresentLogRecord> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        val predicateList = mutableListOf<Predicate>()

        val subQuery = cq.subquery(Long::class.java)
        val subRoot = subQuery.from(MatValue::class.java)
        val subPredicateList = mutableListOf<Predicate>()
        subPredicateList += cb.equal(subRoot.get<PresentLogRecord>(MatValue::presentLogRecord.name), root.get<Long>(PresentLogRecord::id.name))

        predicateList += cb.exists(subQuery)

        form.int(ObjAttr.PRESENT_LOG_RECORD_NUMBER)?.let { predicateList += cb.equal(root.get<Int>(PresentLogRecord::number.name), it) }

        val productName = form.stringNotNull(ObjAttr.PRODUCT_NAME)
        if (productName.isNotBlank()) {
            subPredicateList += cb.like(subRoot.get<Allotment>(MatValue::allotment.name)
                .get<Lot>(Allotment::lot.name)
                .get<LotGroup>(Lot::lotGroup.name)
                .get<Product>(LotGroup::product.name)
                .get(Product::conditionalName.name), "%$productName%"
            )
        }

        form.int(ObjAttr.LETTER_NUMBER)?.let { predicateList += cb.equal(subRoot.get<ProductionShipmentLetter>(MatValue::letter.name).get<Int>(ProductionShipmentLetter::number.name), it) }

        form.int(ObjAttr.CONTRACT_NUMBER)?.let {
            subPredicateList += cb.equal(subRoot.get<Allotment>(MatValue::allotment.name)
                .get<Lot>(Allotment::lot.name)
                .get<LotGroup>(Lot::lotGroup.name)
                .get<ContractSection>(LotGroup::contractSection.name)
                .get<Int>(ContractSection::contract.name)
                .get<Int>(Contract::number.name), it
            )
        }

        val customer = form.stringNotNull(ObjAttr.CUSTOMER)
        if (customer.isNotBlank()) {
            subPredicateList += cb.like(subRoot.get<Allotment>(MatValue::allotment.name)
                .get<Lot>(Allotment::lot.name)
                .get<LotGroup>(Lot::lotGroup.name)
                .get<ContractSection>(LotGroup::contractSection.name)
                .get<Contract>(ContractSection::contract.name)
                .get<Company>(Contract::customer.name)
                .get(Company::name.name), "%$customer%"
            )
        }

        val serialNumber = form.stringNotNull(ObjAttr.SERIAL_NUMBER)
        if (serialNumber.isNotBlank()) { subPredicateList += cb.like(subRoot.get(MatValue::serialNumber.name), "%$serialNumber%") }

        val registrationDate = root.get<LocalDate>(PresentLogRecord::registrationDate.name)
        form.date(ObjAttr.DATE_REGISTERED_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(registrationDate, it) }
        form.date(ObjAttr.DATE_REGISTERED_TO)?.let { predicateList += cb.lessThanOrEqualTo(registrationDate, it) }

        // TODO доделать после того, как будет известна реализация данных дат в erp
/*        form.bool(ObjAttr.OTK_PASSED)?.let {
            predicateList += if (it) {
                cb.isNotNull(root.get<LocalDateTime>(PresentLogRecord::wrappingDate.name))
            } else {
                cb.isNull(root.get<LocalDateTime>(PresentLogRecord::wrappingDate.name))
            }
        }

        form.bool(ObjAttr.PACKAGED)?.let {
            predicateList += if (it) {
                cb.isNull(root.get<LocalDateTime>(PresentLogRecord::shipmentDate.name))
            } else {
                cb.isNull(root.get<LocalDateTime>(PresentLogRecord::shipmentDate.name))
            }
        }*/

        subQuery.select(cb.literal(BaseConstant.ONE_LONG)).where(*subPredicateList.toTypedArray())

        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.PRESENT_LOG_RECORD_NUMBER -> prSort(root.get<Int>(PresentLogRecord::number.name))
                PresentLogRecord::registrationDate.name -> prSort(root.get<LocalDate>(PresentLogRecord::registrationDate.name))
                else -> prSort(root.get<Long>(PresentLogRecord::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = tableInput.start
        typedQuery.maxResults = tableInput.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun findLastLogRecord(): PresentLogRecord? {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        return em.createQuery(cq.select(root).orderBy(
            cb.desc(root.get<Int>(PresentLogRecord::year.name)),
            cb.desc(root.get<Int>(PresentLogRecord::number.name))
        )).setMaxResults(BaseConstant.ONE_INT).resultList.singleOrNull()
    }
}