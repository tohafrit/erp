package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.BaseConstant.ONE_LONG
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.*
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.ProductionShipmentLetterRepository
import ru.korundm.util.KtCommonUtil.typedSingleResult
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface ProductionShipmentLetterService : CommonService<ProductionShipmentLetter> {

    fun getLetterWithMaxNumber(dateFrom: LocalDate, dateTo: LocalDate): ProductionShipmentLetter
    fun findTableData(input: TabrIn, form: DynamicObject): TabrResultQuery<ProductionShipmentLetter>
    fun findListTableData(input: TabrIn, selectedId: Long?, form: DynamicObject): TabrResultQuery<ProductionShipmentLetter>
    fun findLastLetter(): ProductionShipmentLetter?
}

@Service
@Transactional
class ProductionShipmentLetterServiceImpl(
    private val repository: ProductionShipmentLetterRepository
) : ProductionShipmentLetterService {

    private val cl = ProductionShipmentLetter::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductionShipmentLetter> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductionShipmentLetter> = repository.findAllById(idList)

    override fun save(obj: ProductionShipmentLetter): ProductionShipmentLetter {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductionShipmentLetter>): List<ProductionShipmentLetter> = repository.saveAll(objectList)

    override fun read(id: Long): ProductionShipmentLetter? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductionShipmentLetter) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun getLetterWithMaxNumber(dateFrom: LocalDate, dateTo: LocalDate) = repository.findFirstByCreateDateGreaterThanEqualAndCreateDateLessThanEqualOrderByNumberDesc(dateFrom, dateTo)

    override fun findLastLetter(): ProductionShipmentLetter? {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        return em.createQuery(cq.select(root).orderBy(
            cb.desc(root.get<Int>(ProductionShipmentLetter::year.name)),
            cb.desc(root.get<Int>(ProductionShipmentLetter::number.name))
        )).setMaxResults(ONE_INT).typedSingleResult()
    }

    override fun findTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<ProductionShipmentLetter> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        val predicateList = mutableListOf<Predicate>()
        predicateList += cb.isNotNull(root.get<Contract>(ProductionShipmentLetter::sendToProductionDate.name))
        form.int(ObjAttr.NUMBER)?.let { predicateList += cb.equal(root.get<Int>(ProductionShipmentLetter::number.name), it) }

        val productName = form.stringNotNull(ObjAttr.PRODUCT_NAME)
        if (productName.isNotBlank()) {
            val subQuery = cq.subquery(Long::class.java)
            val subRoot = subQuery.from(MatValue::class.java)
            subQuery.select(cb.literal(ONE_LONG)).where(
                cb.equal(subRoot.get<ProductionShipmentLetter>(MatValue::letter.name), root.get<Long>(ProductionShipmentLetter::id.name)),
                cb.like(subRoot.get<Allotment>(MatValue::allotment.name)
                    .get<Lot>(Allotment::lot.name)
                    .get<LotGroup>(Lot::lotGroup.name)
                    .get<Product>(LotGroup::product.name)
                    .get(Product::conditionalName.name), "%$productName%"
                )
            )
            predicateList += cb.exists(subQuery)
        }

        form.int(ObjAttr.CONTRACT_NUMBER)?.let {
            val subQuery = cq.subquery(Long::class.java)
            val subRoot = subQuery.from(MatValue::class.java)
            subQuery.select(cb.literal(ONE_LONG)).where(
                cb.equal(subRoot.get<ProductionShipmentLetter>(MatValue::letter.name), root.get<Long>(ProductionShipmentLetter::id.name)),
                cb.equal(subRoot.get<Allotment>(MatValue::allotment.name)
                    .get<Lot>(Allotment::lot.name)
                    .get<LotGroup>(Lot::lotGroup.name)
                    .get<ContractSection>(LotGroup::contractSection.name)
                    .get<Int>(ContractSection::contract.name)
                    .get<Int>(Contract::number.name), it
                )
            )
            predicateList += cb.exists(subQuery)
        }

        val createDate = root.get<LocalDate>(ProductionShipmentLetter::createDate.name)
        form.date(ObjAttr.CREATE_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(createDate, it) }
        form.date(ObjAttr.CREATE_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(createDate, it) }

        val sendToWarehouseDate = root.get<LocalDate>(ProductionShipmentLetter::sendToWarehouseDate.name)
        form.date(ObjAttr.SEND_TO_WAREHOUSE_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(sendToWarehouseDate, it) }
        form.date(ObjAttr.SEND_TO_WAREHOUSE_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(sendToWarehouseDate, it) }

        val sendToProductionDate = root.get<LocalDate>(ProductionShipmentLetter::sendToProductionDate.name)
        form.date(ObjAttr.SEND_TO_PRODUCTION_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(sendToProductionDate, it) }
        form.date(ObjAttr.SEND_TO_PRODUCTION_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(sendToProductionDate, it) }

        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.NUMBER -> prSort(root.get<Int>(ProductionShipmentLetter::number.name))
                ObjAttr.CREATE_DATE -> prSort(root.get<LocalDate>(ProductionShipmentLetter::createDate.name))
                ProductionShipmentLetter::sendToWarehouseDate.name-> prSort(root.get<LocalDate>(ProductionShipmentLetter::sendToWarehouseDate.name))
                ProductionShipmentLetter::sendToProductionDate.name-> prSort(root.get<LocalDate>(ProductionShipmentLetter::sendToProductionDate.name))
                else -> prSort(root.get<Long>(ProductionShipmentLetter::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun findListTableData(
        input: TabrIn,
        selectedId: Long?,
        form: DynamicObject
    ): TabrResultQuery<ProductionShipmentLetter> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        val predicateList = mutableListOf<Predicate>()
        form.int(ObjAttr.NUMBER)?.let { predicateList += cb.equal(root.get<Int>(ProductionShipmentLetter::number.name), it) }

        val productName = form.stringNotNull(ObjAttr.PRODUCT_NAME)
        if (productName.isNotBlank()) {
            val subQuery = cq.subquery(Long::class.java)
            val subRoot = subQuery.from(MatValue::class.java)
            subQuery.select(cb.literal(ONE_LONG)).where(
                cb.equal(subRoot.get<ProductionShipmentLetter>(MatValue::letter.name), root.get<Long>(ProductionShipmentLetter::id.name)),
                cb.like(subRoot.get<Allotment>(MatValue::allotment.name)
                    .get<Lot>(Allotment::lot.name)
                    .get<LotGroup>(Lot::lotGroup.name)
                    .get<Product>(LotGroup::product.name)
                    .get(Product::conditionalName.name), "%$productName%"
                )
            )
            predicateList += cb.exists(subQuery)
        }

        val contractNumber = form.int(ObjAttr.CONTRACT_NUMBER)
        val contractYear = form.int(ObjAttr.CONTRACT_YEAR)
        if (contractNumber != null || contractYear != null) {
            val subQuery = cq.subquery(Long::class.java)
            val subRoot = subQuery.from(MatValue::class.java)
            val sectionPath = subRoot.get<Allotment>(MatValue::allotment.name)
                .get<Lot>(Allotment::lot.name)
                .get<LotGroup>(Lot::lotGroup.name)
                .get<ContractSection>(LotGroup::contractSection.name)
            val contractPath = sectionPath.get<Contract>(ContractSection::contract.name)

            val subPredicateList = mutableListOf<Predicate>()
            subPredicateList += cb.equal(subRoot.get<ProductionShipmentLetter>(MatValue::letter.name), root.get<Long>(ProductionShipmentLetter::id.name))
            contractNumber?.let { subPredicateList += cb.equal(contractPath.get<Int>(Contract::number.name), it) }
            contractYear?.let { subPredicateList += cb.equal(sectionPath.get<Int>(ContractSection::year.name), it) }
            predicateList += cb.exists(subQuery.select(cb.literal(ONE_LONG)).where(*subPredicateList.toTypedArray()))
        }

        val createDate = root.get<LocalDate>(ProductionShipmentLetter::createDate.name)
        form.date(ObjAttr.CREATE_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(createDate, it) }
        form.date(ObjAttr.CREATE_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(createDate, it) }

        val sendToWarehouseDate = root.get<LocalDate>(ProductionShipmentLetter::sendToWarehouseDate.name)
        form.date(ObjAttr.SEND_TO_WAREHOUSE_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(sendToWarehouseDate, it) }
        form.date(ObjAttr.SEND_TO_WAREHOUSE_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(sendToWarehouseDate, it) }

        val sendToProductionDate = root.get<LocalDate>(ProductionShipmentLetter::sendToProductionDate.name)
        form.date(ObjAttr.SEND_TO_PRODUCTION_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(sendToProductionDate, it) }
        form.date(ObjAttr.SEND_TO_PRODUCTION_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(sendToProductionDate, it) }

        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            if (selectedId == null) {
                val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
                when (it.field) {
                    ObjAttr.NUMBER -> prSort(root.get<Int>(ProductionShipmentLetter::number.name))
                    ObjAttr.CREATE_DATE -> prSort(root.get<LocalDate>(ProductionShipmentLetter::createDate.name))
                    ObjAttr.SEND_TO_WAREHOUSE_DATE -> prSort(root.get<LocalDate>(ProductionShipmentLetter::sendToWarehouseDate.name))
                    ObjAttr.SEND_TO_PRODUCTION_DATE -> prSort(root.get<LocalDate>(ProductionShipmentLetter::sendToProductionDate.name))
                    else -> prSort(root.get<Long>(ObjAttr.ID))
                }
            } else {
                orderList += cb.desc(cb.selectCase<Int>().`when`(cb.equal(root.get<Long>(ObjAttr.ID), selectedId), 1).otherwise(0))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}