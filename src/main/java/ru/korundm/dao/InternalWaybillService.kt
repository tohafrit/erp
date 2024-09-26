package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.configuration.SQLDialect
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.ObjAttr
import ru.korundm.dto.prod.InternalWaybillListDto
import ru.korundm.entity.*
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.InternalWaybillRepository
import ru.korundm.util.KtCommonUtil.typedSingleResult
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface InternalWaybillService : CommonService<InternalWaybill> {

    fun findListTableData(
        input: TabrIn,
        selectedId: Long?,
        form: DynamicObject
    ): TabrResultQuery<InternalWaybillListDto>
    fun getLastByYear(year: Int): InternalWaybill?
}

@Service
@Transactional
class InternalWaybillServiceImpl(
    private val repository: InternalWaybillRepository
) : InternalWaybillService {

    private val cl = InternalWaybill::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<InternalWaybill> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<InternalWaybill> = repository.findAllById(idList)

    override fun save(obj: InternalWaybill): InternalWaybill {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<InternalWaybill>): List<InternalWaybill> = repository.saveAll(objectList)

    override fun read(id: Long): InternalWaybill? = repository.findById(id).orElse(null)

    override fun delete(obj: InternalWaybill) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findListTableData(
        input: TabrIn,
        selectedId: Long?,
        form: DynamicObject
    ): TabrResultQuery<InternalWaybillListDto> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(InternalWaybillListDto::class.java)
        val root = cq.from(cl)
        val rootGiveUser = root.join<InternalWaybill, User>(InternalWaybill::giveUser.name, JoinType.LEFT)
        val rootAcceptUser = root.join<InternalWaybill, User>(InternalWaybill::acceptUser.name, JoinType.LEFT)
        val rootStoragePlace = root.join<InternalWaybill, StoragePlace>(InternalWaybill::storagePlace.name, JoinType.INNER)
        val rootMatValue = root.join<InternalWaybill, MatValue>(InternalWaybill::matValueList.name, JoinType.LEFT)
        val rootPresentLogRecord = rootMatValue.join<MatValue, PresentLogRecord>(MatValue::presentLogRecord.name, JoinType.LEFT)
        val rootProductionShipmentLetter = rootMatValue.join<MatValue, ProductionShipmentLetter>(MatValue::letter.name, JoinType.LEFT)
        val rootAllotment = rootMatValue.join<MatValue, Allotment>(MatValue::allotment.name, JoinType.LEFT)
        val rootLot = rootAllotment.join<Allotment, Lot>(Allotment::lot.name, JoinType.LEFT)
        val rootLotGroup = rootLot.join<Lot, LotGroup>(Lot::lotGroup.name, JoinType.LEFT)
        val rootProduct = rootLotGroup.join<LotGroup, Product>(LotGroup::product.name, JoinType.LEFT)
        val rootContractSection = rootLotGroup.join<LotGroup, ContractSection>(LotGroup::contractSection.name, JoinType.LEFT)
        val rootContract = rootContractSection.join<ContractSection, Contract>(ContractSection::contract.name, JoinType.LEFT)
        val rootCompany = rootContract.join<Contract, Company>(Contract::customer.name, JoinType.LEFT)
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            if (selectedId == null) {
                val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
                when (it.field) {
                    ObjAttr.FULL_NUMBER -> prSort(root.get<Int>(InternalWaybill::number.name))
                    ObjAttr.CREATE_DATE -> prSort(root.get<LocalDate>(InternalWaybill::createDate.name))
                    ObjAttr.ACCEPT_DATE -> prSort(root.get<LocalDate>(InternalWaybill::acceptDate.name))
                    ObjAttr.GIVE_USER -> prSort(rootGiveUser.get<String>(ObjAttr.LAST_NAME))
                    ObjAttr.ACCEPT_USER -> prSort(rootAcceptUser.get<String>(ObjAttr.LAST_NAME))
                    else -> prSort(root.get<Long>(InternalWaybill::id.name))
                }
            } else {
                orderList += cb.desc(cb.selectCase<Int>().`when`(cb.equal(root.get<Long>(ObjAttr.ID), selectedId), 1).otherwise(0))
            }
            cq.orderBy(orderList)
        }
        val predicateList = mutableListOf<Predicate>()
        form.int(ObjAttr.NUMBER)?.let { predicateList += cb.equal(root.get<Int>(InternalWaybill::number.name), it) }
        val productName = form.stringNotNull(ObjAttr.PRODUCT_NAME)
        if (productName.isNotBlank()) predicateList += cb.like(rootProduct.get(Product::conditionalName.name), "%$productName%")
        val serialNumber = form.stringNotNull(ObjAttr.SERIAL_NUMBER)
        if (serialNumber.isNotBlank()) predicateList += cb.like(rootMatValue.get(MatValue::serialNumber.name), "%$serialNumber%")
        val notice = form.stringNotNull(ObjAttr.NOTICE)
        if (notice.isNotBlank()) predicateList += cb.like(rootPresentLogRecord.get(PresentLogRecord::noticeNumber.name), "%$notice%")
        val pathNoticeDate = rootPresentLogRecord.get<LocalDate>(PresentLogRecord::noticeCreateDate.name)
        form.date(ObjAttr.NOTICE_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathNoticeDate, it) }
        form.date(ObjAttr.NOTICE_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathNoticeDate, it) }
        form.int(ObjAttr.LETTER)?.let { predicateList += cb.equal(rootProductionShipmentLetter.get<Int>(ProductionShipmentLetter::number.name), it) }
        form.int(ObjAttr.CONTRACT_NUMBER)?.let { predicateList += cb.equal(rootContract.get<Int>(Contract::number.name), it) }
        form.int(ObjAttr.CONTRACT_YEAR)?.let { predicateList += cb.equal(rootContractSection.get<Int>(ContractSection::year.name), it) }
        form.long(ObjAttr.CUSTOMER_ID)?.let { predicateList += cb.equal(rootCompany.get<Long>(Company::id.name), it) }
        form.int(ObjAttr.STATUS)?.let { status ->
            val pathAcceptDate = root.get<LocalDate>(InternalWaybill::acceptDate.name)
            when (status) {
                1 -> {
                    form.date(ObjAttr.ACCEPT_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathAcceptDate, it) }
                    form.date(ObjAttr.ACCEPT_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathAcceptDate, it) }
                }
                2 -> predicateList += cb.isNull(pathAcceptDate)
                else -> throw IllegalStateException()
            }
        }
        cq.groupBy(
            root.get<Long>(InternalWaybill::id.name),
            root.get<Int>(InternalWaybill::number.name),
            root.get<LocalDate>(InternalWaybill::createDate.name),
            root.get<LocalDate>(InternalWaybill::acceptDate.name),
            rootGiveUser.get<String>(ObjAttr.LAST_NAME),
            rootGiveUser.get<String>(ObjAttr.FIRST_NAME),
            rootGiveUser.get<String>(ObjAttr.MIDDLE_NAME),
            rootAcceptUser.get<String>(ObjAttr.LAST_NAME),
            rootAcceptUser.get<String>(ObjAttr.FIRST_NAME),
            rootAcceptUser.get<String>(ObjAttr.MIDDLE_NAME),
            rootStoragePlace.get<String>(StoragePlace::name.name),
            root.get<String>(InternalWaybill::comment.name)
        )
        val typedQuery = em.createQuery(cq.multiselect(
            root.get<Long>(InternalWaybill::id.name),
            root.get<Int>(InternalWaybill::number.name),
            root.get<LocalDate>(InternalWaybill::createDate.name),
            root.get<LocalDate>(InternalWaybill::acceptDate.name),
            rootGiveUser.get<String>(ObjAttr.LAST_NAME),
            rootGiveUser.get<String>(ObjAttr.FIRST_NAME),
            rootGiveUser.get<String>(ObjAttr.MIDDLE_NAME),
            rootAcceptUser.get<String>(ObjAttr.LAST_NAME),
            rootAcceptUser.get<String>(ObjAttr.FIRST_NAME),
            rootAcceptUser.get<String>(ObjAttr.MIDDLE_NAME),
            rootStoragePlace.get<String>(StoragePlace::name.name),
            root.get<String>(InternalWaybill::comment.name),
            cb.function(SQLDialect.Function.COUNT_OVER, Long::class.java)
        ).where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun getLastByYear(year: Int): InternalWaybill? {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        return em.createQuery(cq.select(root).where(cb.equal(root.get<Int>(InternalWaybill::year.name), year))
            .orderBy(cb.desc(root.get<Int>(InternalWaybill::number.name))))
            .setMaxResults(ONE_INT).typedSingleResult()
    }
}