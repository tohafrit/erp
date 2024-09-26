package ru.korundm.dao

import org.hibernate.query.NativeQuery
import org.hibernate.type.*
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.configuration.SQLDialect
import ru.korundm.configuration.SQLDialect.Function.IFNULL_LONG
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.constant.BaseConstant.ROW_COUNT_ALIAS
import ru.korundm.constant.ObjAttr
import ru.korundm.dto.prod.ShipmentWaybillListAddContractDto
import ru.korundm.entity.*
import ru.korundm.enumeration.ContractType
import ru.korundm.enumeration.Performer
import ru.korundm.helper.*
import ru.korundm.repository.ContractSectionRepository
import ru.korundm.util.KtCommonUtil.ifNotBlank
import ru.korundm.util.KtCommonUtil.resultTransform
import ru.korundm.util.KtCommonUtil.typedManyResult
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

interface ContractSectionService : CommonService<ContractSection> {

    fun existsById(id: Long): Boolean
    fun getByIdentifier(identifier: String): ContractSection
    fun getLastContractSection(contract: Contract): ContractSection
    fun getCountAllByContract(contract: Contract): Int
    fun getFirstSection(contract: Contract): ContractSection
    fun findTableData(tableInput: TabrIn, form: DynamicObject): TabrResultQuery<ContractSection>
    fun <T : RowCountable> findTableData(tableInput: TabrIn, form: DynamicObject, transformCl: KClass<T>): TabrResultQuery<T>
    fun <T : Any> findTableData(letterId: Long, transformCl: KClass<T>): List<T>
    fun findShipmentWaybillAddTableData(input: TabrIn, form: DynamicObject): TabrResultQuery<ShipmentWaybillListAddContractDto>
}

@Service
@Transactional
class ContractSectionServiceImpl(
    private val repository: ContractSectionRepository
) : ContractSectionService {

    private val cl = ContractSection::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ContractSection> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ContractSection> = repository.findAllById(idList)

    override fun save(obj: ContractSection) = repository.save(obj)

    override fun saveAll(objectList: List<ContractSection>): List<ContractSection> = repository.saveAll(objectList)

    override fun read(id: Long): ContractSection? = repository.findById(id).orElse(null)

    override fun delete(obj: ContractSection) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun existsById(id: Long): Boolean = repository.existsById(id)

    override fun getByIdentifier(identifier: String): ContractSection = repository.findFirstByIdentifier(identifier)

    override fun getLastContractSection(contract: Contract): ContractSection = repository.findFirstByContractOrderByIdDesc(contract)

    override fun getCountAllByContract(contract: Contract) : Int = repository.countAllByContract(contract)

    override fun getFirstSection(contract: Contract): ContractSection = repository.findFirstByContract(contract)

    override fun findTableData(
        tableInput: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<ContractSection> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        val predicateList = mutableListOf<Predicate>()
        predicateList += cb.equal(root.get<Contract>(ContractSection::contract.name).get<Long>(Contract::performer.name), Performer.OAOKORUND.id)

        form.int(ObjAttr.NUMBER)?.let {
            predicateList += cb.equal(root.get<Contract>(ContractSection::contract.name).get<Long>(Contract::number.name), it)
        }

        form.string(ObjAttr.CUSTOMER)?.let {
            predicateList += cb.like(root.get<Contract>(ContractSection::contract.name).get<Company>(Contract::customer.name).get(Company::name.name), "%$it%")
        }

        form.bool(ObjAttr.PZ_COPY)?.let {
            predicateList += if (it) {
                cb.isNotNull(root.get<LocalDateTime>(ContractSection::sendToClientDate.name))
            } else {
                cb.isNull(root.get<LocalDateTime>(ContractSection::sendToClientDate.name))
            }
        }

        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.NUMBER -> {
                    prSort(root.get<Int>(ContractSection::year.name))
                    prSort(root.get<Contract>(ContractSection::contract.name).get<Long>(Contract::number.name))
                }
                ObjAttr.CUSTOMER -> prSort(root.get<Contract>(ContractSection::contract.name).get<Company>(Contract::customer.name).get<String>(Company::name.name))
                else -> prSort(root.get<Long>(ContractSection::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = tableInput.start
        typedQuery.maxResults = tableInput.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun <T : RowCountable> findTableData(
        tableInput: TabrIn,
        form: DynamicObject,
        transformCl: KClass<T>
    ): TabrResultQuery<T> {
        val querySort = QuerySort()
        tableInput.sorter?.let { when (it.field) {
            ObjAttr.FULL_NUMBER -> {
                querySort["cs.year"] = if (it.dir == DESC) ASC else DESC
                querySort["c.number"] = if (it.dir == DESC) ASC else DESC
                querySort["cs.number"] = it.dir
            }
            ObjAttr.CUSTOMER -> querySort["comp.name"] = it.dir
            else -> querySort["cs.id"] = it.dir
        }}
        val available = form.bool(ObjAttr.AVAILABLE)
        val pzCopy = form.bool(ObjAttr.PZ_COPY)
        val query = """
            SELECT
                cs.id AS ${ObjAttr.ID},
                c.number AS ${ObjAttr.CONTRACT_NUMBER},
                c.type AS ${ObjAttr.TYPE},
                c.performer AS ${ObjAttr.PERFORMER},
                cs.create_date AS ${ObjAttr.CREATE_DATE},
                cs.year AS ${ObjAttr.YEAR},
                cs.number AS ${ObjAttr.NUMBER},
                cs.send_to_client_date AS ${ObjAttr.PZ_COPY_DATE}, 
                cs.send_to_client_date IS NOT NULL AS ${ObjAttr.PZ_COPY},
                comp.name AS ${ObjAttr.NAME},
                comp.location AS ${ObjAttr.LOCATION},
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM contracts c
            JOIN contract_sections cs
            ON c.id = cs.contract_id
            JOIN companies comp
            ON comp.id = c.customer_id
            WHERE
                c.performer = :performer
                AND cs.archive_date IS NULL
                AND (:contractNumber IS NULL OR :contractNumber IS NOT NULL AND c.number = :contractNumber)
                AND (:customer = '' OR :customer <> '' AND comp.name = :customer)
                AND (:pzCopy IS NULL OR :pzCopy IS NOT NULL AND IF(:pzCopy = 1, cs.send_to_client_date IS NOT NULL, cs.send_to_client_date IS NULL))
                AND (:available IS NULL OR :available IS NOT NULL AND ${if (available == true) "NOT" else ""} EXISTS (
                    SELECT 1
                    FROM lot_groups lg
                    JOIN lots l 
                    ON l.lot_group_id = lg.id
                    JOIN allotment al
                    ON al.lot_id = l.id
                    JOIN mat_value mv
                    ON mv.allotment_id = al.id
                    WHERE lg.contract_section_id = cs.id
                ))
            ${querySort.queryString(true)}
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.PERFORMER, Performer.OAOKORUND.id)
            .setParameter(ObjAttr.CONTRACT_NUMBER, form.long(ObjAttr.CONTRACT_NUMBER))
            .setParameter(ObjAttr.CUSTOMER, form.stringNotNull(ObjAttr.CUSTOMER).ifNotBlank { "%$it%" })
            .setParameter(ObjAttr.PZ_COPY, pzCopy)
            .setParameter(ObjAttr.AVAILABLE, available)
        nativeQuery.firstResult = tableInput.start
        nativeQuery.maxResults = tableInput.size
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.CONTRACT_NUMBER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.TYPE, IntegerType.INSTANCE)
            .addScalar(ObjAttr.PERFORMER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.CREATE_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.PZ_COPY_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.YEAR, IntegerType.INSTANCE)
            .addScalar(ObjAttr.NUMBER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.LOCATION, StringType.INSTANCE)
            .addScalar(ObjAttr.PZ_COPY, BooleanType.INSTANCE)
            .addScalar(ROW_COUNT_ALIAS, LongType.INSTANCE)
            .resultTransform(transformCl)
        return TabrResultQuery.instance(nativeQuery.typedManyResult())
    }

    override fun <T : Any> findTableData(
        letterId: Long,
        transformCl: KClass<T>
    ): List<T> {
        val query = """
            SELECT 
                cs.id AS ${ObjAttr.ID},
                c.number AS ${ObjAttr.CONTRACT_NUMBER},
                c.performer AS ${ObjAttr.PERFORMER},
                c.type AS ${ObjAttr.TYPE},
                cs.year AS ${ObjAttr.YEAR},
                cs.number AS ${ObjAttr.NUMBER},
                comp.name AS ${ObjAttr.NAME},
                p.conditional_name AS ${ObjAttr.PRODUCT_NAME},
                l.delivery_date AS ${ObjAttr.DELIVERY_DATE},
                lg.order_index AS ${ObjAttr.ORDER_INDEX},
                l.accept_type AS ${ObjAttr.ACCEPT_TYPE},
                l.special_test_type AS ${ObjAttr.SPECIAL_TEST_TYPE},
                COUNT(cs.id) AS ${ObjAttr.AMOUNT}
            FROM 
                mat_value mv
                JOIN allotment a
                ON mv.allotment_id = a.id
                JOIN lots l
                ON a.lot_id = l.id
                JOIN lot_groups lg
                ON l.lot_group_id = lg.id
                JOIN contract_sections cs
                ON lg.contract_section_id = cs.id
                JOIN contracts c
                ON cs.contract_id = c.id
                JOIN companies comp
                ON c.customer_id = comp.id
                JOIN products p
                ON lg.product_id = p.id
            WHERE mv.letter_id = :letterId
            GROUP BY 
                cs.id,
                c.number,
                c.performer,
                c.type,
                cs.year,
                cs.number,
                comp.name,
                p.conditional_name,
                l.delivery_date,
                lg.order_index,
                l.accept_type,
                l.special_test_type
            ORDER BY comp.name
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.LETTER_ID, letterId)
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.CONTRACT_NUMBER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.PERFORMER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.TYPE, IntegerType.INSTANCE)
            .addScalar(ObjAttr.YEAR, IntegerType.INSTANCE)
            .addScalar(ObjAttr.NUMBER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.PRODUCT_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.DELIVERY_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.ORDER_INDEX, LongType.INSTANCE)
            .addScalar(ObjAttr.ACCEPT_TYPE, IntegerType.INSTANCE)
            .addScalar(ObjAttr.SPECIAL_TEST_TYPE, LongType.INSTANCE)
            .addScalar(ObjAttr.AMOUNT, LongType.INSTANCE)
            .resultTransform(transformCl)
        return nativeQuery.typedManyResult()
    }

    override fun findShipmentWaybillAddTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<ShipmentWaybillListAddContractDto> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(ShipmentWaybillListAddContractDto::class.java)
        val root = cq.from(cl)
        val rootContract = root.join<ContractSection, Contract>(ContractSection::contract.name)
        val rootCompany = rootContract.join<Contract, Company>(Contract::customer.name, JoinType.LEFT)
        val rootLotGroup = root.join<ContractSection, LotGroup>(ContractSection::lotGroupList.name, JoinType.LEFT)
        val rootLot = rootLotGroup.join<LotGroup, Lot>(LotGroup::lotList.name, JoinType.LEFT)
        val rootAllotment = rootLot.join<Lot, Allotment>(Lot::allotmentList.name, JoinType.LEFT)
        val rootMatValue = rootAllotment.join<Allotment, MatValue>(Allotment::matValueList.name, JoinType.LEFT)
        val rootLetter = rootMatValue.join<MatValue, ProductionShipmentLetter>(MatValue::letter.name, JoinType.LEFT)
        rootMatValue.on(
            rootMatValue.get<InternalWaybill>(MatValue::internalWaybill.name).isNotNull,
            rootMatValue.get<ShipmentWaybill>(MatValue::shipmentWaybill.name).isNull
        )
        rootLetter.on(rootLetter.get<LocalDate>(ProductionShipmentLetter::sendToWarehouseDate.name).isNotNull)
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.NUMBER -> {
                    prSort(root.get<Int>(ContractSection::year.name))
                    prSort(rootContract.get<Int>(Contract::number.name))
                    orderList += cb.asc(root.get<Int>(ContractSection::number.name))
                }
                ObjAttr.EXTERNAL_NUMBER -> prSort(root.get<String>(ContractSection::externalNumber.name))
                ObjAttr.CREATE_DATE -> prSort(root.get<String>(ContractSection::createDate.name))
                else -> prSort(root.get<Long>(ContractSection::id.name))
            }
            cq.orderBy(orderList)
        }
        val predicateList = mutableListOf<Predicate>()
        predicateList += rootContract.get<ContractType>(Contract::type.name).`in`(listOf(
            ContractType.PRODUCT_SUPPLY, ContractType.SUPPLY_OF_EXPORTED, ContractType.SCIENTIFIC_AND_TECHNICAL,
            ContractType.INTERNAL_APPLICATION, ContractType.OTHER, ContractType.DESIGN_DOCUMENTATION,
            ContractType.SERVICES, ContractType.ORDER_WITHOUT_EXECUTION_OF_THE_CONTRACT,
            ContractType.REPAIR_PZ, ContractType.REPAIR_OTK
        ))
        predicateList += cb.equal(rootContract.get<Performer>(Contract::performer.name), Performer.OAOKORUND)
        predicateList += root.get<LocalDate>(ContractSection::archiveDate.name).isNull
        val pathCreateDate = root.get<LocalDate>(ContractSection::createDate.name)
        form.date(ObjAttr.CREATE_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathCreateDate, it) }
        form.date(ObjAttr.CREATE_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathCreateDate, it) }
        form.int(ObjAttr.NUMBER)?.let { predicateList += cb.equal(rootContract.get<Int>(Contract::number.name), it) }
        form.int(ObjAttr.YEAR)?.let { predicateList += cb.equal(root.get<Int>(ContractSection::year.name), it) }
        form.long(ObjAttr.CUSTOMER_ID)?.let { predicateList += cb.equal(rootCompany.get<Int>(Company::id.name), it) }
        val externalNumber = form.stringNotNull(ObjAttr.EXTERNAL_NUMBER)
        if (externalNumber.isNotBlank()) predicateList += cb.like(root.get(ContractSection::externalNumber.name), "%$externalNumber%")

        cq.groupBy(
            root.get<Long>(ContractSection::id.name),
            rootContract.get<Int>(Contract::number.name),
            rootContract.get<Long>(Contract::performer.name),
            rootContract.get<Long>(Contract::type.name),
            root.get<Int>(ContractSection::year.name),
            root.get<Int>(ContractSection::number.name),
            root.get<String>(ContractSection::externalNumber.name),
            root.get<Long>(ContractSection::createDate.name),
            rootCompany.get<String>(Company::name.name),
            rootCompany.get<Long>(Company::id.name)
        )

        val readyPredicate = cb.sum(cb.function(IFNULL_LONG, Long::class.java, rootLetter.get<Long>(ProductionShipmentLetter::id.name), cb.literal(0)))
        val multiselect = cq.multiselect(
            root.get<Long>(ContractSection::id.name),
            rootContract.get<Int>(Contract::number.name),
            rootContract.get<Long>(Contract::performer.name),
            rootContract.get<Long>(Contract::type.name),
            root.get<Int>(ContractSection::year.name),
            root.get<Int>(ContractSection::number.name),
            root.get<String>(ContractSection::externalNumber.name),
            root.get<Long>(ContractSection::createDate.name),
            rootCompany.get<String>(Company::name.name),
            rootCompany.get<Long>(Company::id.name),
            cb.greaterThan(readyPredicate, 0),
            cb.function(SQLDialect.Function.COUNT_OVER, Long::class.java)
        )
        form.bool(ObjAttr.READY)?.let { multiselect.having(if (it) cb.greaterThan(readyPredicate, 0) else cb.equal(readyPredicate, 0)) }

        val typedQuery = em.createQuery(multiselect.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}