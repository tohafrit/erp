package ru.korundm.dao

import org.hibernate.query.NativeQuery
import org.hibernate.type.*
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.configuration.SQLDialect
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.constant.BaseConstant.ROW_COUNT_ALIAS
import ru.korundm.constant.ObjAttr
import ru.korundm.dto.prod.*
import ru.korundm.entity.*
import ru.korundm.enumeration.ContractType
import ru.korundm.enumeration.Performer
import ru.korundm.enumeration.PriceKindType
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.QuerySort
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.MatValueRepository
import ru.korundm.util.KtCommonUtil.nullIfBlankOr
import ru.korundm.util.KtCommonUtil.resultTransform
import ru.korundm.util.KtCommonUtil.typedManyResult
import java.sql.Date
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.TemporalType
import javax.persistence.criteria.JoinType.LEFT
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface MatValueService : CommonService<MatValue> {

    fun deleteAll(list: List<MatValue>)
    fun getBySerialNumber(serialNumber: String): MatValue?
    fun getAllByProductionShipmentLetter(letter: ProductionShipmentLetter): List<MatValue>
    fun getAllByPresentLogRecord(logRecord: PresentLogRecord?): List<MatValue>
    fun getAllByAllotment(allotment: Allotment?): List<MatValue>
    fun getAllByInternalWaybillId(id: Long?): List<MatValue>
    fun getAllByShipmentWaybillId(id: Long?): List<MatValue>
    fun findInternalWaybillTableData(input: TabrIn, waybillId: Long): TabrResultQuery<InternalWaybillMatValueDto>
    fun findInternalWaybillCandidateTableData(input: TabrIn, form: DynamicObject): TabrResultQuery<InternalWaybillMatValueAddDto>
    fun findShipmentWaybillTableData(waybillId: Long): List<ShipmentWaybillMatValueDto>
    fun findCheckShipmentWaybillTableData(waybillId: Long): List<ShipmentWaybillCheckShipmentDto>
    fun getAllByPresentLogRecordIdList(idList: List<Long>): List<MatValue>
    fun findShipmentWaybillCandidateTableData(input: TabrIn, contractSectionId: Long, form: DynamicObject): List<ShipmentWaybillMatValueAddDto>
    fun findWarehouseStateTableData(form: DynamicObject): List<WarehouseStateListDto>
    fun findWarehouseStateMatValueTableData(input: TabrIn, form: DynamicObject, productId: Long): TabrResultQuery<WarehouseStateMatValueDto>
    fun findWarehouseResidueDocData(): List<WarehouseStateResidueDocDto>
    fun getAllByProduct(product: Product?): List<MatValue>
    fun existsByInternalWaybillId(id: Long?): Boolean
    fun existsByShipmentWaybillId(id: Long?): Boolean
    fun findWarehouseReceiptShipmentProductPeriodData(productId: Long, dateFrom: LocalDate, dateTo: LocalDate): List<WarehouseStateReportFirstDocDto>
    fun findWarehouseMonthlyShipmentReportData(date: LocalDate): List<WarehouseStateReportSecondDocDto>
}

@Service
@Transactional
class MatValueServiceImpl(
    private val repository: MatValueRepository
) : MatValueService {

    private val cl = MatValue::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<MatValue> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<MatValue> = repository.findAllById(idList)

    override fun save(obj: MatValue): MatValue {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<MatValue>): List<MatValue> = repository.saveAll(objectList)

    override fun read(id: Long): MatValue? = repository.findById(id).orElse(null)

    override fun delete(obj: MatValue) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun getBySerialNumber(serialNumber: String) = repository.findBySerialNumber(serialNumber)

    override fun getAllByProductionShipmentLetter(letter: ProductionShipmentLetter) = repository.findAllByLetter(letter)

    override fun deleteAll(list: List<MatValue>) = repository.deleteAll(list)

    override fun getAllByPresentLogRecord(logRecord: PresentLogRecord?) = logRecord?.let { repository.findAllByPresentLogRecord(it) } ?: emptyList()

    override fun getAllByAllotment(allotment: Allotment?) = allotment?.let { repository.findAllByAllotment(it) } ?: emptyList()

    override fun getAllByInternalWaybillId(id: Long?) = if (id == null) emptyList() else repository.findAllByInternalWaybillId(id)

    override fun getAllByShipmentWaybillId(id: Long?) = if (id == null) emptyList() else repository.findAllByShipmentWaybillId(id)

    override fun getAllByProduct(product: Product?) = product?.let { repository.findAllByAllotmentLotLotGroupProduct(it) } ?: emptyList()

    override fun findInternalWaybillTableData(
        input: TabrIn,
        waybillId: Long
    ): TabrResultQuery<InternalWaybillMatValueDto> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(InternalWaybillMatValueDto::class.java)
        val root = cq.from(cl)
        val rootCell = root.join<MatValue, StorageCell>(MatValue::cell.name, LEFT)
        val rootAllotment = root.join<MatValue, Allotment>(MatValue::allotment.name)
        val rootLot = rootAllotment.join<Allotment, Lot>(Allotment::lot.name)
        val rootLotGroup = rootLot.join<Lot, LotGroup>(Lot::lotGroup.name)
        val rootProduct = rootLotGroup.join<LotGroup, Product>(LotGroup::product.name)
        val rootContractSection = rootLotGroup.join<LotGroup, ContractSection>(LotGroup::contractSection.name)
        val rootContract = rootContractSection.join<ContractSection, Contract>(ContractSection::contract.name)
        val rootCompany = rootContract.join<Contract, Company>(Contract::customer.name, LEFT)
        val rootPresentLogRecord = root.join<MatValue, PresentLogRecord>(MatValue::presentLogRecord.name)
        val rootLetter = root.join<MatValue, ProductionShipmentLetter>(MatValue::letter.name)
        val rootIntWaybill = root.join<MatValue, InternalWaybill>(MatValue::internalWaybill.name)
        val rootStoragePlace = rootIntWaybill.join<InternalWaybill, StoragePlace>(InternalWaybill::storagePlace.name)
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            orderList += cb.asc(rootProduct.get<String>(Product::conditionalName.name))
            val prSort = { path: Path<*> -> orderList += if (ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.SERIAL_NUMBER -> prSort(root.get<String>(MatValue::serialNumber.name))
                ObjAttr.CELL -> prSort(rootCell.get<String>(StorageCell::name.name))
                ObjAttr.CONTRACT -> {
                    prSort(rootContractSection.get<Int>(ContractSection::year.name))
                    prSort(rootContract.get<Int>(Contract::number.name))
                    orderList += cb.asc(rootContractSection.get<Int>(ContractSection::number.name))
                }
                ObjAttr.CUSTOMER -> prSort(rootCompany.get<String>(Company::name.name))
                ObjAttr.NOTICE -> prSort(rootPresentLogRecord.get<String>(PresentLogRecord::noticeNumber.name))
                ObjAttr.NOTICE_DATE -> prSort(rootPresentLogRecord.get<LocalDate>(PresentLogRecord::noticeCreateDate.name))
                ObjAttr.STATEMENT -> {
                    prSort(rootPresentLogRecord.get<Int>(PresentLogRecord::conformityStatementYear.name))
                    prSort(rootPresentLogRecord.get<String>(PresentLogRecord::conformityStatementNumber.name))
                }
                ObjAttr.LETTER -> prSort(rootLetter.get<Int>(ProductionShipmentLetter::number.name))
                ObjAttr.LOCATION -> prSort(rootStoragePlace.get<String>(StoragePlace::name.name))
                else -> prSort(root.get<Long>(MatValue::id.name))
            }
            cq.orderBy(orderList)
        }
        val predicateList = mutableListOf<Predicate>()
        predicateList += cb.equal(rootIntWaybill.get<Long>(InternalWaybill::id.name), waybillId)
        val typedQuery = em.createQuery(cq.multiselect(
            root.get<Long>(MatValue::id.name),
            rootProduct.get<String>(Product::conditionalName.name),
            root.get<String>(MatValue::serialNumber.name),
            rootCell.get<String>(StorageCell::name.name),
            rootLetter.get<Long>(ProductionShipmentLetter::id.name),
            rootContract.get<Long>(Contract::id.name),
            rootContractSection.get<Long>(ContractSection::id.name),
            rootContract.get<Int>(Contract::number.name),
            rootContract.get<Performer>(Contract::performer.name),
            rootContract.get<ContractType>(Contract::type.name),
            rootContractSection.get<Int>(ContractSection::year.name),
            rootContractSection.get<Int>(ContractSection::number.name),
            rootCompany.get<String>(Company::name.name),
            rootPresentLogRecord.get<String>(PresentLogRecord::noticeNumber.name),
            rootPresentLogRecord.get<LocalDate>(PresentLogRecord::noticeCreateDate.name),
            rootPresentLogRecord.get<String>(PresentLogRecord::conformityStatementNumber.name),
            rootLetter.get<Int>(ProductionShipmentLetter::number.name),
            rootStoragePlace.get<String>(StoragePlace::name.name),
            cb.function(SQLDialect.Function.COUNT_OVER, Long::class.java)
        ).where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun findInternalWaybillCandidateTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<InternalWaybillMatValueAddDto> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(InternalWaybillMatValueAddDto::class.java)
        val root = cq.from(cl)
        val rootAllotment = root.join<MatValue, Allotment>(MatValue::allotment.name)
        val rootLot = rootAllotment.join<Allotment, Lot>(Allotment::lot.name)
        val rootLotGroup = rootLot.join<Lot, LotGroup>(Lot::lotGroup.name)
        val rootProduct = rootLotGroup.join<LotGroup, Product>(LotGroup::product.name)
        val rootPresentLogRecord = root.join<MatValue, PresentLogRecord>(MatValue::presentLogRecord.name)
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.NOTICE -> prSort(rootPresentLogRecord.get<String>(PresentLogRecord::noticeNumber.name))
                ObjAttr.PRODUCT -> prSort(rootProduct.get<String>(Product::conditionalName.name))
                ObjAttr.NOTICE_DATE -> prSort(rootPresentLogRecord.get<LocalDate>(PresentLogRecord::noticeCreateDate.name))
                else -> prSort(root.get<Long>(ObjAttr.ID))
            }
            cq.orderBy(orderList)
        }
        val predicateList = mutableListOf<Predicate>()
        predicateList += root.get<InternalWaybill>(MatValue::internalWaybill.name).isNull

        val notice = form.stringNotNull(ObjAttr.NOTICE)
        if (notice.isNotBlank()) predicateList += cb.like(rootPresentLogRecord.get(PresentLogRecord::noticeNumber.name), "%$notice%")
        val productName = form.stringNotNull(ObjAttr.PRODUCT)
        if (productName.isNotBlank()) predicateList += cb.like(rootProduct.get(Product::conditionalName.name), "%$productName%")
        val pathNoticeDate = rootPresentLogRecord.get<LocalDate>(PresentLogRecord::noticeCreateDate.name)
        form.date(ObjAttr.NOTICE_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathNoticeDate, it) }
        form.date(ObjAttr.NOTICE_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathNoticeDate, it) }
        val pathPackDate = root.get<LocalDate>(MatValue::packedDate.name)
        form.bool(ObjAttr.PACKED)?.let { predicateList += if (it) pathPackDate.isNotNull else pathPackDate.isNull }
        val pathTechControlDate = root.get<LocalDate>(MatValue::technicalControlDate.name)
        form.bool(ObjAttr.TECH_CONTROLLED)?.let { predicateList += if (it) pathTechControlDate.isNotNull else pathTechControlDate.isNull }

        cq.groupBy(
            rootPresentLogRecord.get<Long>(PresentLogRecord::id.name),
            rootProduct.get<Long>(Product::id.name),
            rootPresentLogRecord.get<String>(PresentLogRecord::noticeNumber.name),
            rootProduct.get<String>(Product::conditionalName.name),
            rootPresentLogRecord.get<LocalDate>(PresentLogRecord::noticeCreateDate.name),
            root.get<LocalDate>(MatValue::packedDate.name),
            root.get<LocalDate>(MatValue::technicalControlDate.name)
        )

        val typedQuery = em.createQuery(cq.multiselect(
            rootPresentLogRecord.get<Long>(PresentLogRecord::id.name),
            rootProduct.get<Long>(Product::id.name),
            rootPresentLogRecord.get<String>(PresentLogRecord::noticeNumber.name),
            rootProduct.get<String>(Product::conditionalName.name),
            cb.function(SQLDialect.Function.COUNT_INT, Int::class.java, root.get<Long>(MatValue::id.name)),
            cb.function(SQLDialect.Function.GROUP_CONCAT, String::class.java, root.get<String>(MatValue::serialNumber.name), cb.literal(", ")),
            rootPresentLogRecord.get<LocalDate>(PresentLogRecord::noticeCreateDate.name),
            root.get<LocalDate>(MatValue::packedDate.name),
            root.get<LocalDate>(MatValue::technicalControlDate.name),
            cb.function(SQLDialect.Function.COUNT_OVER, Long::class.java)
        ).where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun findShipmentWaybillTableData(waybillId: Long): List<ShipmentWaybillMatValueDto> {
        val query = """
            SELECT
                res.id,
                res.productId,
                res.productName,
                res.serialNumber,
                COUNT(res.id) OVER(PARTITION BY res.productId, res.price) AS amount,
                res.price,
                res.shipped,
                res.checked,
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM (
                SELECT
                    mv.id AS id,
                    p.id AS productId,
                    p.conditional_name AS productName,
                    mv.serial_number AS serialNumber,
                    CAST(IFNULL((
                        CASE 
                            WHEN l.price_kind = :preliminaryPriceKind OR l.price_kind = :exportPriceKind
                            THEN l.price
                            WHEN l.price_kind = :finalPriceKind
                            THEN IF(pcp.price = 0, pcp.price_unpack, pcp.price)
                            ELSE 0
                        END
                    ), 0) AS DECIMAL(14, 2)) AS price,
                    mv.shipment_waybill_date IS NOT NULL AS shipped,
                    mv.shipment_checked  AS checked
                FROM
                    mat_value mv
                    JOIN
                    allotment a ON mv.allotment_id = a.id
                    JOIN
                    lots l ON a.lot_id = l.id
                    JOIN
                    lot_groups lg ON l.lot_group_id = lg.id
                    JOIN
                    products p ON lg.product_id = p.id
                    LEFT JOIN
                    product_charges_protocol pcp ON pcp.id = l.protocol_id
                WHERE
                    mv.shipment_waybill_id = :waybillId
            ) res
            ORDER BY
                res.productName,
                res.price
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.PRELIMINARY_PRICE_KIND, PriceKindType.PRELIMINARY.id)
            .setParameter(ObjAttr.EXPORT_PRICE_KIND, PriceKindType.EXPORT.id)
            .setParameter(ObjAttr.FINAL_PRICE_KIND, PriceKindType.FINAL.id)
            .setParameter(ObjAttr.WAYBILL_ID, waybillId)
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.PRODUCT_ID, LongType.INSTANCE)
            .addScalar(ObjAttr.PRODUCT_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.SERIAL_NUMBER, StringType.INSTANCE)
            .addScalar(ObjAttr.AMOUNT, IntegerType.INSTANCE)
            .addScalar(ObjAttr.PRICE, DoubleType.INSTANCE)
            .addScalar(ObjAttr.SHIPPED, BooleanType.INSTANCE)
            .addScalar(ObjAttr.CHECKED, BooleanType.INSTANCE)
            .addScalar(ROW_COUNT_ALIAS, LongType.INSTANCE)
            .resultTransform(ShipmentWaybillMatValueDto::class)
        return nativeQuery.typedManyResult()
    }

    override fun findCheckShipmentWaybillTableData(waybillId: Long): List<ShipmentWaybillCheckShipmentDto> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(ShipmentWaybillCheckShipmentDto::class.java)
        val root = cq.from(cl)
        val rootAllotment = root.join<MatValue, Allotment>(MatValue::allotment.name)
        val rootLot = rootAllotment.join<Allotment, Lot>(Allotment::lot.name)
        val rootLotGroup = rootLot.join<Lot, LotGroup>(Lot::lotGroup.name)
        val rootProduct = rootLotGroup.join<LotGroup, Product>(LotGroup::product.name)
        val rootCell = root.join<MatValue, StorageCell>(MatValue::cell.name, LEFT)

        val predicateList = mutableListOf<Predicate>()
        predicateList += cb.equal(root.get<ShipmentWaybill>(MatValue::shipmentWaybill.name), waybillId)

        cq.orderBy(cb.asc(rootProduct.get<String>(Product::conditionalName.name)))

        val typedQuery = em.createQuery(cq.multiselect(
            root.get<Long>(MatValue::id.name),
            rootProduct.get<Long>(Product::id.name),
            rootProduct.get<String>(Product::conditionalName.name),
            root.get<String>(MatValue::serialNumber.name),
            rootCell.get<String>(StorageCell::name.name),
            root.get<Boolean>(MatValue::shipmentChecked.name)
        ).where(*predicateList.toTypedArray()))
        return typedQuery.resultList
    }

    override fun getAllByPresentLogRecordIdList(idList: List<Long>) = repository.findAllByPresentLogRecordIdIn(idList)

    override fun findShipmentWaybillCandidateTableData(
        input: TabrIn,
        contractSectionId: Long,
        form: DynamicObject
    ): List<ShipmentWaybillMatValueAddDto> {
        val querySort = QuerySort()
        input.sorter?.let { when (it.field) {
            ObjAttr.PRODUCT, ObjAttr.NOTICE -> querySort[it.field] = it.dir
            ObjAttr.INTERNAL_WAYBILL -> querySort[ObjAttr.INT_WAYBILL_NUMBER] = it.dir
            else -> querySort[ObjAttr.ID] = it.dir
        }}
        val query = """
            SELECT
                res.id,
                res.product,
                res.notice,
                res.intWaybillNumber,
                res.amount,
                res.serialNumber,
                res.ready
            FROM (
                SELECT
                    plr.id AS id,
                    p.conditional_name AS product,
                    plr.notice_number AS notice,
                    plr.notice_create_date AS noticeDate,
                    iw.number AS intWaybillNumber,
                    COUNT(mv.id) AS amount,
                    GROUP_CONCAT(mv.serial_number SEPARATOR ', ') AS serialNumber,
                    iw.id IS NOT NULL AND sw.id IS NULL AND (psl.send_to_warehouse_date IS NOT NULL OR mv.permit_for_shipment_date IS NOT NULL) AS ready
                FROM
                    mat_value mv
                    JOIN
                    present_log_record plr ON mv.present_log_record_id = plr.id
                    JOIN
                    allotment a ON mv.allotment_id = a.id
                    JOIN
                    lots l ON a.lot_id = l.id
                    JOIN
                    lot_groups lg ON l.lot_group_id = lg.id
                    JOIN
                    products p ON lg.product_id = p.id
                    JOIN
                    internal_waybill iw ON mv.internal_waybill_id = iw.id
                    LEFT JOIN
                    shipment_waybill sw ON mv.shipment_waybill_id = sw.id
                    JOIN
                    production_shipment_letter psl ON mv.letter_id = psl.id
                    JOIN
                    contract_sections cs ON lg.contract_section_id = cs.id
                WHERE
                    cs.id = :sectionId
                    AND mv.shipment_waybill_id IS NULL
                GROUP BY
                    plr.id,
                    plr.notice_number,
                    plr.notice_create_date,
                    p.id,
                    p.conditional_name,
                    iw.id,
                    sw.id,
                    cs.id,
                    psl.send_to_warehouse_date,
                    mv.permit_for_shipment_date,
                    iw.number
            ) res
            WHERE
                (:notice IS NULL OR :notice IS NOT NULL AND res.notice LIKE :notice)
                AND (:productName IS NULL OR :productName IS NOT NULL AND res.product LIKE :productName)
                AND (:serialNumber IS NULL OR :serialNumber IS NOT NULL AND res.serialNumber LIKE :serialNumber)
                AND (:ready IS NULL OR :ready IS NOT NULL AND res.ready = :ready)
                AND (:noticeDateFrom IS NULL OR :noticeDateFrom IS NOT NULL AND res.noticeDate >= :noticeDateFrom)
                AND (:noticeDateTo IS NULL OR :noticeDateTo IS NOT NULL AND res.noticeDate <= :noticeDateTo)
            ${querySort.queryString(true)}
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.SECTION_ID, contractSectionId)
            .setParameter(ObjAttr.NOTICE, form.string(ObjAttr.NOTICE).nullIfBlankOr { "%${it}%" })
            .setParameter(ObjAttr.PRODUCT_NAME, form.string(ObjAttr.PRODUCT_NAME).nullIfBlankOr { "%${it}%" })
            .setParameter(ObjAttr.SERIAL_NUMBER, form.string(ObjAttr.SERIAL_NUMBER).nullIfBlankOr { "%${it}%" })
            .setParameter(ObjAttr.NOTICE_DATE_FROM, form.date(ObjAttr.NOTICE_DATE_FROM)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.NOTICE_DATE_TO, form.date(ObjAttr.NOTICE_DATE_TO)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.READY, form.bool(ObjAttr.READY))
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.PRODUCT, StringType.INSTANCE)
            .addScalar(ObjAttr.NOTICE, StringType.INSTANCE)
            .addScalar(ObjAttr.INT_WAYBILL_NUMBER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.AMOUNT, IntegerType.INSTANCE)
            .addScalar(ObjAttr.SERIAL_NUMBER, StringType.INSTANCE)
            .addScalar(ObjAttr.READY, BooleanType.INSTANCE)
            .resultTransform(ShipmentWaybillMatValueAddDto::class)
        return nativeQuery.typedManyResult()
    }

    override fun findWarehouseStateTableData(form: DynamicObject): List<WarehouseStateListDto> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(WarehouseStateListDto::class.java)
        val root = cq.from(cl)
        val rootAllotment = root.join<MatValue, Allotment>(MatValue::allotment.name)
        val rootLot = rootAllotment.join<Allotment, Lot>(Allotment::lot.name)
        val rootLotGroup = rootLot.join<Lot, LotGroup>(Lot::lotGroup.name)
        val rootProduct = rootLotGroup.join<LotGroup, Product>(LotGroup::product.name)
        val rootInternalWaybill = root.join<MatValue, InternalWaybill>(MatValue::internalWaybill.name)
        val rootContractSection = rootLotGroup.join<LotGroup, ContractSection>(LotGroup::contractSection.name)
        val rootContract = rootContractSection.join<ContractSection, Contract>(ContractSection::contract.name)
        val rootCompany = rootContract.join<Contract, Company>(Contract::customer.name, LEFT)
        val rootLetter = root.join<MatValue, ProductionShipmentLetter>(MatValue::letter.name, LEFT)
        val rootCell = root.join<MatValue, StorageCell>(MatValue::cell.name, LEFT)

        val predicateList = mutableListOf<Predicate>()
        predicateList += root.get<LocalDate>(MatValue::internalWaybillDate.name).isNotNull
        val productName = form.stringNotNull(ObjAttr.PRODUCT)
        if (productName.isNotBlank()) predicateList += cb.like(rootProduct.get(Product::conditionalName.name), "%$productName%")
        val serialNumber = form.stringNotNull(ObjAttr.SERIAL_NUMBER)
        if (serialNumber.isNotBlank()) predicateList += cb.like(root.get(MatValue::serialNumber.name), "%$serialNumber%")
        form.int(ObjAttr.CONTRACT_NUMBER)?.let { predicateList += cb.equal(rootContract.get<Int>(Contract::number.name), it) }
        form.int(ObjAttr.CONTRACT_YEAR)?.let { predicateList += cb.equal(rootContractSection.get<Int>(ContractSection::year.name), it) }
        form.long(ObjAttr.CUSTOMER_ID)?.let { predicateList += cb.equal(rootCompany.get<Int>(Company::id.name), it) }
        form.int(ObjAttr.LETTER)?.let { predicateList += cb.equal(rootLetter.get<Int>(ProductionShipmentLetter::number.name), it) }
        val cell = form.stringNotNull(ObjAttr.CELL)
        if (cell.isNotBlank()) predicateList += cb.like(rootCell.get(StorageCell::name.name), "$cell%")
        val shipped = form.boolNotNull(ObjAttr.SHIPPED)
        if (shipped) {
            predicateList += root.get<LocalDate>(MatValue::shipmentWaybillDate.name).isNotNull
            val pathShipmentDate = root.get<LocalDate>(MatValue::shipmentWaybillDate.name)
            form.date(ObjAttr.SHIPMENT_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathShipmentDate, it) }
            form.date(ObjAttr.SHIPMENT_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathShipmentDate, it) }
        } else {
            predicateList += root.get<LocalDate>(MatValue::shipmentWaybillDate.name).isNull
            form.long(ObjAttr.PLACE_ID)?.let { predicateList += cb.equal(rootInternalWaybill.get<StoragePlace>(InternalWaybill::storagePlace.name), it) }
            val pathReceiptDate = root.get<LocalDate>(MatValue::internalWaybillDate.name)
            form.date(ObjAttr.RECEIPT_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathReceiptDate, it) }
            form.date(ObjAttr.RECEIPT_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathReceiptDate, it) }
        }

        cq.groupBy(
            rootProduct.get<Long>(Product::id.name),
            rootProduct.get<String>(Product::conditionalName.name)
        )
        cq.orderBy(cb.asc(rootProduct.get<String>(Product::conditionalName.name)))
        val typedQuery = em.createQuery(cq.multiselect(
            rootProduct.get<Long>(Product::id.name),
            rootProduct.get<String>(Product::conditionalName.name),
            cb.function(SQLDialect.Function.COUNT_INT, Int::class.java, root.get<Long>(MatValue::id.name))
        ).where(*predicateList.toTypedArray()))
        return typedQuery.resultList
    }

    override fun findWarehouseStateMatValueTableData(
        input: TabrIn,
        form: DynamicObject,
        productId: Long
    ): TabrResultQuery<WarehouseStateMatValueDto> {
        val querySort = QuerySort()
        input.sorter?.let { when (it.field) {
            ObjAttr.SERIAL_NUMBER -> querySort["mv.serial_number"] = it.dir
            ObjAttr.CONTRACT -> {
                querySort["cs.year"] = it.dir
                querySort["c.number"] = it.dir
                querySort["cs.number"] = ASC
            }
            ObjAttr.CUSTOMER -> querySort["customer.name"] = it.dir
            ObjAttr.LETTER -> querySort["psl.number"] = it.dir
            ObjAttr.INTERNAL_WAYBILL -> querySort["iw.number"] = it.dir
            ObjAttr.ACCEPT_DATE -> querySort["iw.accept_date"] = it.dir
            ObjAttr.SHIPMENT_DATE -> querySort["mv.shipment_waybill_date"] = it.dir
            else -> querySort["mv.id"] = it.dir
        }}
        val query = """
            SELECT
                mv.id AS id,
                mv.serial_number AS serialNumber,
                c.number AS contractNumber,
                c.performer AS contractPerformer,
                c.type AS contractType,
                cs.year AS sectionYear,
                cs.number AS sectionNumber,
                customer.name AS customer,
                psl.number AS letter,
                l.accept_type AS acceptTypeId,
                plr.notice_number AS noticeNumber,
                plr.notice_create_date AS noticeDate,
                iw.number AS internalWaybillNumber,
                iw.accept_date AS acceptDate,
                CAST(IFNULL((
                    CASE 
                        WHEN l.price_kind = :preliminaryPriceKind OR l.price_kind = :exportPriceKind
                        THEN l.price
                        WHEN l.price_kind = :finalPriceKind
                        THEN IF(pcp.price = 0, pcp.price_unpack, pcp.price)
                        ELSE 0
                    END
                ), 0) AS DECIMAL(14, 2)) AS price,
                mv.shipment_waybill_date AS shipmentDate,
                sc.name AS cell,
                sp.name AS place,
                :shipped AS shipped,
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM
                mat_value mv
                JOIN
                allotment a ON mv.allotment_id = a.id
                JOIN
                lots l ON a.lot_id = l.id
                JOIN
                lot_groups lg ON l.lot_group_id = lg.id
                JOIN
                contract_sections cs ON lg.contract_section_id = cs.id
                JOIN
                contracts c ON cs.contract_id = c.id
                LEFT JOIN
                companies customer ON c.customer_id = customer.id
                JOIN
                production_shipment_letter psl ON mv.letter_id = psl.id
                JOIN
                present_log_record plr ON mv.present_log_record_id = plr.id
                JOIN
                internal_waybill iw ON mv.internal_waybill_id = iw.id
                LEFT JOIN
                product_charges_protocol pcp ON pcp.id = l.protocol_id
                JOIN
                storage_place sp ON sp.id = iw.storage_place_id
                JOIN
                products p ON lg.product_id = p.id
                LEFT JOIN
                storage_cell sc ON mv.storage_cell_id = sc.id
            WHERE
                mv.internal_waybill_date IS NOT NULL
                AND p.id = :productId
                AND IF(:shipped = 1, mv.shipment_waybill_date IS NOT NULL, TRUE)
                AND IF(:shipped = 1, (
                    (:shipmentDateFrom IS NULL OR :shipmentDateFrom IS NOT NULL AND mv.shipment_waybill_date >= :shipmentDateFrom)
                    AND (:shipmentDateTo IS NULL OR :shipmentDateTo IS NOT NULL AND mv.shipment_waybill_date <= :shipmentDateTo)
                ), TRUE)
                AND IF(:shipped = 0, mv.shipment_waybill_date IS NULL, TRUE)
                AND IF(:shipped = 0, (:placeId IS NULL OR :placeId IS NOT NULL AND sp.id = :placeId), TRUE)
                AND IF(:shipped = 0, (
                    (:receiptDateFrom IS NULL OR :receiptDateFrom IS NOT NULL AND mv.internal_waybill_date >= :receiptDateFrom)
                    AND (:receiptDateTo IS NULL OR :receiptDateTo IS NOT NULL AND mv.internal_waybill_date <= :receiptDateTo)
                ), TRUE)
                AND (:serialNumber IS NULL OR :serialNumber IS NOT NULL AND mv.serial_number LIKE :serialNumber)
                AND (:contractNumber IS NULL OR :contractNumber IS NOT NULL AND c.number = :contractNumber)
                AND (:contractYear IS NULL OR :contractYear IS NOT NULL AND cs.year = :contractYear)
                AND (:customerId IS NULL OR :customerId IS NOT NULL AND customer.id = :customerId)
                AND (:letter IS NULL OR :letter IS NOT NULL AND psl.number = :letter)
                AND (:cell IS NULL OR :cell IS NOT NULL AND sc.name LIKE :cell)
            ${querySort.queryString(true)}
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.PRELIMINARY_PRICE_KIND, PriceKindType.PRELIMINARY.id)
            .setParameter(ObjAttr.EXPORT_PRICE_KIND, PriceKindType.EXPORT.id)
            .setParameter(ObjAttr.FINAL_PRICE_KIND, PriceKindType.FINAL.id)
            .setParameter(ObjAttr.PRODUCT_ID, productId)
            .setParameter(ObjAttr.SHIPPED, form.boolNotNull(ObjAttr.SHIPPED))
            .setParameter(ObjAttr.SHIPMENT_DATE_FROM, form.date(ObjAttr.SHIPMENT_DATE_FROM)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.SHIPMENT_DATE_TO, form.date(ObjAttr.SHIPMENT_DATE_TO)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.PLACE_ID, form.long(ObjAttr.PLACE_ID))
            .setParameter(ObjAttr.RECEIPT_DATE_FROM, form.date(ObjAttr.RECEIPT_DATE_FROM)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.RECEIPT_DATE_TO, form.date(ObjAttr.RECEIPT_DATE_TO)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.SERIAL_NUMBER, form.string(ObjAttr.SERIAL_NUMBER).nullIfBlankOr { "%${it}%" })
            .setParameter(ObjAttr.CONTRACT_NUMBER, form.int(ObjAttr.CONTRACT_NUMBER))
            .setParameter(ObjAttr.CONTRACT_YEAR, form.int(ObjAttr.CONTRACT_YEAR))
            .setParameter(ObjAttr.CUSTOMER_ID, form.long(ObjAttr.CUSTOMER_ID))
            .setParameter(ObjAttr.LETTER, form.int(ObjAttr.LETTER))
            .setParameter(ObjAttr.CELL, form.string(ObjAttr.CELL).nullIfBlankOr { "${it}%" })
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.SERIAL_NUMBER, StringType.INSTANCE)
            .addScalar(ObjAttr.CONTRACT_NUMBER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.CONTRACT_PERFORMER, LongType.INSTANCE)
            .addScalar(ObjAttr.CONTRACT_TYPE, LongType.INSTANCE)
            .addScalar(ObjAttr.SECTION_YEAR, IntegerType.INSTANCE)
            .addScalar(ObjAttr.SECTION_NUMBER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.CUSTOMER, StringType.INSTANCE)
            .addScalar(ObjAttr.LETTER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.ACCEPT_TYPE_ID, LongType.INSTANCE)
            .addScalar(ObjAttr.NOTICE_NUMBER, StringType.INSTANCE)
            .addScalar(ObjAttr.NOTICE_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.INTERNAL_WAYBILL_NUMBER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.ACCEPT_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.PRICE, DoubleType.INSTANCE)
            .addScalar(ObjAttr.SHIPMENT_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.CELL, StringType.INSTANCE)
            .addScalar(ObjAttr.PLACE, StringType.INSTANCE)
            .addScalar(ObjAttr.SHIPPED, BooleanType.INSTANCE)
            .addScalar(ROW_COUNT_ALIAS, LongType.INSTANCE)
            .resultTransform(WarehouseStateMatValueDto::class)
        return TabrResultQuery.instance(nativeQuery.typedManyResult())
    }

    override fun findWarehouseResidueDocData(): List<WarehouseStateResidueDocDto> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(WarehouseStateResidueDocDto::class.java)
        val root = cq.from(cl)
        val rootAllotment = root.join<MatValue, Allotment>(MatValue::allotment.name)
        val rootLot = rootAllotment.join<Allotment, Lot>(Allotment::lot.name)
        val rootLotGroup = rootLot.join<Lot, LotGroup>(Lot::lotGroup.name)
        val rootProduct = rootLotGroup.join<LotGroup, Product>(LotGroup::product.name)
        val rootContractSection = rootLotGroup.join<LotGroup, ContractSection>(LotGroup::contractSection.name)
        val rootContract = rootContractSection.join<ContractSection, Contract>(ContractSection::contract.name)
        val rootPresentLogRecord = root.join<MatValue, PresentLogRecord>(MatValue::presentLogRecord.name)
        val rootCompany = rootContract.join<Contract, Company>(Contract::customer.name, LEFT)
        val rootCell = root.join<MatValue, StorageCell>(MatValue::cell.name, LEFT)

        val predicateList = mutableListOf<Predicate>()
        predicateList += root.get<LocalDate>(MatValue::internalWaybillDate.name).isNotNull
        predicateList += root.get<LocalDate>(MatValue::shipmentWaybillDate.name).isNull

        cq.orderBy(
            cb.asc(rootProduct.get<String>(Product::conditionalName.name)),
            cb.asc(rootCompany.get<String>(Company::name.name)),
            cb.desc(rootContractSection.get<Int>(ContractSection::year.name)),
            cb.desc(rootContract.get<Int>(Contract::number.name)),
            cb.asc(rootContractSection.get<Int>(ContractSection::number.name)),
            cb.asc(root.get<String>(MatValue::serialNumber.name))
        )
        val typedQuery = em.createQuery(cq.multiselect(
            rootProduct.get<Long>(Product::id.name),
            rootProduct.get<String>(Product::conditionalName.name),
            root.get<String>(MatValue::serialNumber.name),
            rootCompany.get<String>(Company::name.name),
            rootContract.get<Int>(Contract::number.name),
            rootContract.get<Performer>(Contract::performer.name),
            rootContract.get<ContractType>(Contract::type.name),
            rootContractSection.get<Int>(ContractSection::year.name),
            rootContractSection.get<Int>(ContractSection::number.name),
            rootCell.get<String>(StorageCell::name.name),
            rootPresentLogRecord.get<String>(PresentLogRecord::noticeNumber.name),
            rootPresentLogRecord.get<LocalDate>(PresentLogRecord::noticeCreateDate.name)
        ).where(*predicateList.toTypedArray()))
        return typedQuery.resultList
    }

    override fun existsByInternalWaybillId(id: Long?) = if (id == null) false else repository.existsByInternalWaybillId(id)

    override fun existsByShipmentWaybillId(id: Long?) = if (id == null) false else repository.existsByShipmentWaybillId(id)

    override fun findWarehouseReceiptShipmentProductPeriodData(
        productId: Long,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): List<WarehouseStateReportFirstDocDto> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(WarehouseStateReportFirstDocDto::class.java)
        val root = cq.from(cl)
        val rootAllotment = root.join<MatValue, Allotment>(MatValue::allotment.name)
        val rootLot = rootAllotment.join<Allotment, Lot>(Allotment::lot.name)
        val rootLotGroup = rootLot.join<Lot, LotGroup>(Lot::lotGroup.name)
        val rootProduct = rootLotGroup.join<LotGroup, Product>(LotGroup::product.name)
        val rootContractSection = rootLotGroup.join<LotGroup, ContractSection>(LotGroup::contractSection.name)
        val rootContract = rootContractSection.join<ContractSection, Contract>(ContractSection::contract.name)
        val rootPresentLogRecord = root.join<MatValue, PresentLogRecord>(MatValue::presentLogRecord.name)
        val rootCompany = rootContract.join<Contract, Company>(Contract::customer.name)
        val rootIntWaybill = root.join<MatValue, InternalWaybill>(MatValue::internalWaybill.name)
        val rootShipmentWaybill = root.join<MatValue, ShipmentWaybill>(MatValue::shipmentWaybill.name, LEFT)

        val predicateList = mutableListOf<Predicate>()
        val datePath = rootIntWaybill.get<LocalDate>(InternalWaybill::acceptDate.name)
        predicateList += cb.greaterThanOrEqualTo(datePath, dateFrom)
        predicateList += cb.lessThanOrEqualTo(datePath, dateTo)
        predicateList += cb.equal(rootProduct.get<Long>(ObjAttr.ID), productId)

        cq.orderBy(
            cb.asc(rootCompany.get<String>(Company::name.name)),
            cb.asc(rootContract.get<Performer>(Contract::performer.name)),
            cb.asc(rootContract.get<ContractType>(Contract::type.name)),
            cb.asc(rootContract.get<Int>(Contract::number.name)),
            cb.asc(rootContractSection.get<Int>(ContractSection::number.name)),
            cb.desc(rootIntWaybill.get<LocalDate>(InternalWaybill::acceptDate.name)),
            cb.desc(rootIntWaybill.get<Int>(InternalWaybill::number.name)),
            cb.desc(rootPresentLogRecord.get<Int>(PresentLogRecord::number.name)),
            cb.asc(root.get<String>(MatValue::serialNumber.name))
        )
        val typedQuery = em.createQuery(cq.multiselect(
            rootContract.get<Int>(Contract::number.name),
            rootContract.get<Performer>(Contract::performer.name),
            rootContract.get<ContractType>(Contract::type.name),
            rootContractSection.get<Int>(ContractSection::year.name),
            rootContractSection.get<Int>(ContractSection::number.name),
            rootIntWaybill.get<LocalDate>(InternalWaybill::acceptDate.name),
            rootIntWaybill.get<Int>(InternalWaybill::number.name),
            rootPresentLogRecord.get<Int>(PresentLogRecord::number.name),
            rootPresentLogRecord.get<LocalDate>(PresentLogRecord::registrationDate.name),
            root.get<String>(MatValue::serialNumber.name),
            rootShipmentWaybill.get<LocalDate>(ShipmentWaybill::shipmentDate.name),
            rootShipmentWaybill.get<Int>(ShipmentWaybill::number.name),
            rootCompany.get<String>(Company::name.name)
        ).where(*predicateList.toTypedArray()))
        val resList = typedQuery.resultList
        /*resList += WarehouseStateReportFirstDocDto(94, Performer.OAOKORUND, ContractType.INTERNAL_APPLICATION, 2012, 2, LocalDate.of(2015, 11, 11), 237, 1799, LocalDate.of(2015, 11, 10), "935504320005", LocalDate.of(2015, 11, 12), 607, "OAO")
        resList += WarehouseStateReportFirstDocDto(141, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2011, 0, LocalDate.of(2012, 8, 15), 282, 1004, LocalDate.of(2012, 8, 13), "925504320031", LocalDate.of(2012, 8, 24), 198, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(141, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2011, 0, LocalDate.of(2012, 8, 15), 282, 1006, LocalDate.of(2012, 8, 14), "925504320032", LocalDate.of(2012, 8, 24), 198, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(141, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2011, 1, LocalDate.of(2012, 6, 15), 225, 679, LocalDate.of(2012, 6, 14), "925504320030", LocalDate.of(2012, 7, 4), 112, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 0, LocalDate.of(2014, 12, 17), 278, 1932, LocalDate.of(2014, 12, 17), "925504320052", LocalDate.of(2014, 12, 19), 666, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 0, LocalDate.of(2014, 7, 18), 146, 1052, LocalDate.of(2014, 7, 18), "925504320048", LocalDate.of(2014, 7, 18), 345, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 0, LocalDate.of(2014, 7, 18), 146, 1052, LocalDate.of(2014, 7, 18), "925504320050", LocalDate.of(2014, 7, 18), 345, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 0, LocalDate.of(2014, 6, 30), 131, 943, LocalDate.of(2014, 6, 30), "935504320003", LocalDate.of(2014, 7, 1), 313, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 0, LocalDate.of(2014, 5, 28), 100, 761, LocalDate.of(2014, 5, 27), "935504320002", LocalDate.of(2014, 5, 28), 243, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 1, LocalDate.of(2015, 7, 20), 147, 1007, LocalDate.of(2015, 6, 19), "925504320062", LocalDate.of(2015, 7, 20), 375, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 1, LocalDate.of(2015, 7, 20), 147, 1007, LocalDate.of(2015, 6, 19), "925504320063", LocalDate.of(2015, 7, 20), 375, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 1, LocalDate.of(2015, 6, 24), 125, 1016, LocalDate.of(2015, 6, 23), "925504320064", LocalDate.of(2015, 6, 24), 322, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 1, LocalDate.of(2015, 5, 7), 92, 743, LocalDate.of(2015, 5, 7), "925504320059", LocalDate.of(2015, 5, 22), 244, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 1, LocalDate.of(2015, 3, 19), 56, 451, LocalDate.of(2015, 3, 19), "925504320057", LocalDate.of(2015, 4, 7), 174, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 1, LocalDate.of(2015, 2, 12), 28, 203, LocalDate.of(2015, 2, 11), "925504320054", LocalDate.of(2015, 2, 19), 72, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 1, LocalDate.of(2015, 2, 11), 26, 195, LocalDate.of(2015, 2, 10), "925504320053", LocalDate.of(2015, 2, 19), 72, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 2, LocalDate.of(2015, 12, 16), 273, 2074, LocalDate.of(2015, 12, 15), "935504320008", LocalDate.of(2015, 12, 21), 692, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 2, LocalDate.of(2015, 12, 16), 273, 2074, LocalDate.of(2015, 12, 15), "935504320009", LocalDate.of(2015, 12, 21), 692, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 2, LocalDate.of(2015, 4, 27), 86, 684, LocalDate.of(2015, 4, 24), "925504320060", LocalDate.of(2015, 11, 30), 640, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 2, LocalDate.of(2015, 4, 27), 86, 684, LocalDate.of(2015, 4, 24), "925504320061", LocalDate.of(2015, 11, 30), 640, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 2, LocalDate.of(2015, 4, 10), 76, 615, LocalDate.of(2015, 4, 9), "925504320058", LocalDate.of(2015, 11, 30), 640, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 2, LocalDate.of(2015, 3, 19), 56, 452, LocalDate.of(2015, 3, 19), "925504320055", LocalDate.of(2015, 11, 30), 640, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(309, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 2, LocalDate.of(2015, 3, 19), 56, 452, LocalDate.of(2015, 3, 19), "925504320056", LocalDate.of(2015, 11, 2), 592, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 0, LocalDate.of(2013, 12, 5), 249, 1860, LocalDate.of(2013, 12, 4), "925504320044", LocalDate.of(2014, 1, 21), 13, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 0, LocalDate.of(2013, 11, 13), 234, 1697, LocalDate.of(2013, 11, 13), "925504320042", LocalDate.of(2013, 11, 27), 566, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 0, LocalDate.of(2013, 10, 18), 218, 1533, LocalDate.of(2013, 10, 17), "925504320041", LocalDate.of(2013, 10, 31), 510, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 0, LocalDate.of(2013, 9, 27), 200, 1387, LocalDate.of(2013, 9, 26), "925504320040", LocalDate.of(2013, 10, 3), 447, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 0, LocalDate.of(2013, 8, 20), 172, 1137, LocalDate.of(2013, 8, 19), "925504320039", LocalDate.of(2013, 9, 5), 383, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 0, LocalDate.of(2013, 8, 15), 170, 1135, LocalDate.of(2013, 8, 15), "925504320038", LocalDate.of(2013, 8, 19), 354, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 0, LocalDate.of(2013, 8, 2), 158, 1052, LocalDate.of(2013, 8, 2), "925504320037", LocalDate.of(2013, 8, 2), 328, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 0, LocalDate.of(2013, 7, 22), 150, 995, LocalDate.of(2013, 7, 19), "925504320036", LocalDate.of(2013, 7, 26), 314, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 0, LocalDate.of(2013, 5, 31), 112, 726, LocalDate.of(2013, 5, 30), "925504320035", LocalDate.of(2013, 6, 4), 225, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 1, LocalDate.of(2014, 7, 18), 146, 1051, LocalDate.of(2014, 7, 18), "925504320049", LocalDate.of(2014, 7, 18), 346, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 1, LocalDate.of(2014, 7, 18), 146, 1051, LocalDate.of(2014, 7, 18), "925504320051", LocalDate.of(2014, 7, 18), 346, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 1, LocalDate.of(2014, 7, 1), 133, 959, LocalDate.of(2014, 7, 1), "935504320004", LocalDate.of(2014, 7, 1), 315, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 1, LocalDate.of(2014, 4, 30), 82, 637, LocalDate.of(2014, 4, 30), "935504320001", LocalDate.of(2014, 5, 21), 227, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 1, LocalDate.of(2014, 4, 3), 57, 405, LocalDate.of(2014, 4, 1), "925504320047", LocalDate.of(2014, 4, 17), 164, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 1, LocalDate.of(2014, 2, 25), 30, 235, LocalDate.of(2014, 2, 25), "925504320046", LocalDate.of(2014, 3, 24), 112, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 1, LocalDate.of(2014, 2, 10), 15, 159, LocalDate.of(2014, 2, 7), "925504320045", LocalDate.of(2014, 2, 25), 68, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(55, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2013, 2, LocalDate.of(2013, 11, 14), 235, 1713, LocalDate.of(2013, 11, 14), "925504320043", LocalDate.of(2013, 11, 27), 567, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(77, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2015, 0, LocalDate.of(2015, 11, 20), 245, 1865, LocalDate.of(2015, 11, 20), "935504320007", LocalDate.of(2015, 11, 20), 628, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(77, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2015, 0, LocalDate.of(2015, 11, 19), 243, 1852, LocalDate.of(2015, 11, 18), "935504320006", LocalDate.of(2015, 11, 20), 628, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(77, Performer.OAOKORUND, ContractType.PRODUCT_SUPPLY, 2015, 0, LocalDate.of(2015, 11, 11), 237, 1799, LocalDate.of(2015, 11, 10), "935504320005", LocalDate.of(2015, 11, 12), 607, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(90, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 0, LocalDate.of(2013, 5, 17), 100, 638, LocalDate.of(2013, 5, 16), "925504320033", LocalDate.of(2013, 6, 4), 224, "АО \"ГРПЗ\"")
        resList += WarehouseStateReportFirstDocDto(90, Performer.OAOKORUND, ContractType.SUPPLY_OF_EXPORTED, 2013, 0, LocalDate.of(2013, 5, 17), 100, 638, LocalDate.of(2013, 5, 16), "925504320034", LocalDate.of(2013, 6, 4), 224, "АО \"ГРПЗ\"")*/
        return resList
    }

    override fun findWarehouseMonthlyShipmentReportData(date: LocalDate): List<WarehouseStateReportSecondDocDto> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(WarehouseStateReportSecondDocDto::class.java)
        val root = cq.from(cl)
        val rootAllotment = root.join<MatValue, Allotment>(MatValue::allotment.name)
        val rootLot = rootAllotment.join<Allotment, Lot>(Allotment::lot.name)
        val rootLotGroup = rootLot.join<Lot, LotGroup>(Lot::lotGroup.name)
        val rootProduct = rootLotGroup.join<LotGroup, Product>(LotGroup::product.name)
        val rootContractSection = rootLotGroup.join<LotGroup, ContractSection>(LotGroup::contractSection.name)
        val rootContract = rootContractSection.join<ContractSection, Contract>(ContractSection::contract.name)
        val rootCompany = rootContract.join<Contract, Company>(Contract::customer.name)

        val predicateList = mutableListOf<Predicate>()
        val datePath = root.get<LocalDate>(MatValue::shipmentWaybillDate.name)
        predicateList += cb.greaterThanOrEqualTo(datePath, date.withDayOfMonth(1))
        predicateList += cb.lessThan(datePath, date.plusMonths(1).withDayOfMonth(1))

        cq.orderBy(
            cb.desc(root.get<LocalDate>(MatValue::shipmentWaybillDate.name)),
            cb.asc(rootContractSection.get<String>(ContractSection::externalNumber.name)),
            cb.asc(rootContractSection.get<Int>(ContractSection::year.name)),
            cb.asc(rootContract.get<Int>(Contract::number.name)),
            cb.asc(rootContractSection.get<Int>(ContractSection::number.name)),
            cb.asc(rootProduct.get<String>(Product::productionName.name)),
            cb.asc(root.get<String>(MatValue::serialNumber.name))
        )

        val typedQuery = em.createQuery(cq.multiselect(
            rootProduct.get<String>(Product::productionName.name),
            root.get<String>(MatValue::serialNumber.name),
            rootContractSection.get<String>(ContractSection::externalNumber.name),
            rootContract.get<Int>(Contract::number.name),
            rootContract.get<Performer>(Contract::performer.name),
            rootContract.get<ContractType>(Contract::type.name),
            rootContractSection.get<Int>(ContractSection::year.name),
            rootContractSection.get<Int>(ContractSection::number.name),
            rootCompany.get<String>(Company::name.name),
            root.get<LocalDate>(MatValue::shipmentWaybillDate.name),
        ).where(*predicateList.toTypedArray()))
        val resList = typedQuery.resultList
        /*resList += WarehouseStateReportSecondDocDto("Модуль МКИО БТ62-401В ЮКСУ.467555.004-02", "062401330140", "2022187222692432208211763/329/КБ-ПП-20", 1, Performer.KORUND, ContractType.M, 1, 1, "АО \"Радиоприбор\"", LocalDate.of(2021, 10, 29))
        resList += WarehouseStateReportSecondDocDto("Модуль МКИО БТ62-401В ЮКСУ.467555.004-02", "062401330143", "2022187222692432208211763/329/КБ-ПП-20", 1, Performer.KORUND, ContractType.M, 1, 1, "АО \"Радиоприбор\"", LocalDate.of(2021, 10, 29))
        resList += WarehouseStateReportSecondDocDto("Модуль МКИО БТ62-401В ЮКСУ.467555.004-02", "062401330145", "2022187222692432208211763/329/КБ-ПП-20", 1, Performer.KORUND, ContractType.M, 1, 1, "АО \"Радиоприбор\"", LocalDate.of(2021, 10, 29))
        resList += WarehouseStateReportSecondDocDto("Модуль МКИО БТ62-401В ЮКСУ.467555.004-02", "062401330146", "2022187222692432208211763/329/КБ-ПП-20", 1, Performer.KORUND, ContractType.M, 1, 1, "АО \"Радиоприбор\"", LocalDate.of(2021, 10, 29))
        resList += WarehouseStateReportSecondDocDto("ЭВМ \"Багет-33-02\" ЮКСУ.466225.009-01ТУ с комплектом блока вентилятора ЮКСУ.466929.009, вариант упаковки 2", "983302100592", "2022187222692432208211763/329/КБ-ПП-20", 1, Performer.KORUND, ContractType.M, 1, 1, "АО \"Радиоприбор\"", LocalDate.of(2021, 10, 29))
        resList += WarehouseStateReportSecondDocDto("ЭВМ \"Багет-33-02\" ЮКСУ.466225.009-01ТУ с комплектом блока вентилятора ЮКСУ.466929.009, вариант упаковки 2", "983302100593", "2022187222692432208211763/329/КБ-ПП-20", 1, Performer.KORUND, ContractType.M, 1, 1, "АО \"Радиоприбор\"", LocalDate.of(2021, 10, 29))*/
        return resList
    }
}