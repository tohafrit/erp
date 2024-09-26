package ru.korundm.dao

import org.hibernate.query.NativeQuery
import org.hibernate.type.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.constant.BaseConstant.ROW_COUNT_ALIAS
import ru.korundm.constant.ObjAttr
import ru.korundm.dto.prod.ShipmentWaybillListDto
import ru.korundm.entity.ShipmentWaybill
import ru.korundm.enumeration.PriceKindType
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.QuerySort
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.ShipmentWaybillRepository
import ru.korundm.util.KtCommonUtil.nullIfBlankOr
import ru.korundm.util.KtCommonUtil.resultTransform
import ru.korundm.util.KtCommonUtil.typedManyResult
import ru.korundm.util.KtCommonUtil.typedSingleResult
import java.sql.Date
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.TemporalType

interface ShipmentWaybillService : CommonService<ShipmentWaybill> {

    fun findListTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<ShipmentWaybillListDto>
    fun getLastByYear(year: Int): ShipmentWaybill?
}

@Service
@Transactional
class ShipmentWaybillServiceImpl(
    private val repository: ShipmentWaybillRepository
) : ShipmentWaybillService {

    private val cl = ShipmentWaybill::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ShipmentWaybill> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ShipmentWaybill> = repository.findAllById(idList)

    override fun save(obj: ShipmentWaybill): ShipmentWaybill {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ShipmentWaybill>): List<ShipmentWaybill> = repository.saveAll(objectList)

    override fun read(id: Long): ShipmentWaybill? = repository.findById(id).orElse(null)

    override fun delete(obj: ShipmentWaybill) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findListTableData(input: TabrIn, form: DynamicObject): TabrResultQuery<ShipmentWaybillListDto> {
        val querySort = QuerySort()
        input.sorter?.let { when (it.field) {
            ObjAttr.NUMBER, ObjAttr.LETTER_NUMBER, ObjAttr.CREATE_DATE, ObjAttr.SHIPMENT_DATE -> querySort[it.field] = it.dir
            else -> querySort[ObjAttr.ID] = it.dir
        } }
        val query = """
            SELECT
                res.id,
                res.number,
                res.createDate,
                res.shipmentDate,
                res.externalNumber,
                res.contractNumber,
                res.contractPerformer,
                res.contractType,
                res.sectionYear,
                res.sectionNumber,
                res.account,
                res.payer,
                res.consignee,
                GROUP_CONCAT(DISTINCT res.vatName SEPARATOR ', ') AS vat,
                ROUND(SUM(res.amount * res.price), 2) AS totalWoVat,
                ROUND(SUM(res.amount * res.price * (1 + res.vatValue/100)), 2) AS totalVat,
                res.giveUserLastName,
                res.giveUserFirstName,
                res.giveUserMiddleName,
                res.permitUserLastName,
                res.permitUserFirstName,
                res.permitUserMiddleName,
                res.accountantUserLastName,
                res.accountantUserFirstName,
                res.accountantUserMiddleName,
                res.transmittalLetter,
                res.receiver,
                res.letterOfAttorney,
                res.comment,
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM (
                SELECT
                    sw.id AS id,
                    sw.number AS number,
                    sw.create_date AS createDate,
                    sw.shipment_date AS shipmentDate,
                    p.conditional_name AS productName,
                    p.decimal_number AS serialNumber,
                    cs.external_number AS externalNumber,
                    c.number AS contractNumber,
                    c.performer AS contractPerformer,
                    c.type AS contractType,
                    cs.year AS sectionYear,
                    cs.number AS sectionNumber,
                    acc.account AS account,
                    payer.id AS payerId,
                    payer.name AS payer,
                    consignee.id AS consigneeId,
                    consignee.name AS consignee,
                    vat.name AS vatName,
                    IFNULL(vat.value, 0) AS vatValue,
                    IF(mv.id IS NULL, 0, 1) AS amount,
                    CAST(IFNULL((
                        CASE 
                            WHEN l.price_kind = :preliminaryPriceKind OR l.price_kind = :exportPriceKind
                            THEN l.price
                            WHEN l.price_kind = :finalPriceKind
                            THEN IF(pcp.price = 0, pcp.price_unpack, pcp.price)
                            ELSE 0
                        END
                    ), 0) AS DECIMAL(14, 2)) AS price,
                    give_user.last_name AS giveUserLastName,
                    give_user.first_name AS giveUserFirstName,
                    give_user.middle_name AS giveUserMiddleName,
                    permit_user.last_name AS permitUserLastName,
                    permit_user.first_name AS permitUserFirstName,
                    permit_user.middle_name AS permitUserMiddleName,
                    accountant_user.last_name AS accountantUserLastName,
                    accountant_user.first_name AS accountantUserFirstName,
                    accountant_user.middle_name AS accountantUserMiddleName,
                    sw.transmittal_letter AS transmittalLetter,
                    sw.receiver AS receiver,
                    sw.letter_of_attorney AS letterOfAttorney,
                    sw.comment AS comment
                FROM
                    shipment_waybill sw
                    JOIN
                    contract_sections cs ON sw.contract_section_id = cs.id
                    JOIN
                    contracts c ON cs.contract_id = c.id
                    LEFT JOIN
                    accounts acc ON sw.account_id = acc.id
                    LEFT JOIN
                    companies payer ON c.customer_id = payer.id
                    LEFT JOIN
                    companies consignee ON sw.consignee_id = consignee.id
                    LEFT JOIN
                    users give_user ON sw.give_user_id = give_user.id
                    LEFT JOIN
                    users permit_user ON sw.permit_user_id = permit_user.id
                    LEFT JOIN
                    users accountant_user ON sw.accountant_user_id = accountant_user.id
                    LEFT JOIN
                    mat_value mv ON mv.shipment_waybill_id = sw.id
                    LEFT JOIN
                    allotment allot ON allot.id = mv.allotment_id
                    LEFT JOIN
                    lots l ON allot.lot_id = l.id
                    LEFT JOIN
                    value_added_tax vat ON l.vat_id = vat.id
                    LEFT JOIN
                    product_charges_protocol pcp ON l.protocol_id = pcp.id
                    LEFT JOIN
                    lot_groups lg on l.lot_group_id = lg.id
                    LEFT JOIN
                    products p ON p.id = lg.product_id
                    LEFT JOIN
                    present_log_record plr ON plr.id = mv.present_log_record_id
                    LEFT JOIN
                    production_shipment_letter psl ON psl.id = mv.letter_id
            ) res
            WHERE
                (:productName IS NULL OR :productName IS NOT NULL AND res.productName LIKE :productName)
                AND (:serialNumber IS NULL OR :serialNumber IS NOT NULL AND res.serialNumber LIKE :serialNumber)
                AND (:number IS NULL OR :number IS NOT NULL AND res.number = :number)
                AND (
                    (:createDateFrom IS NULL OR :createDateFrom IS NOT NULL AND res.createDate >= :createDateFrom)
                    AND (:createDateTo IS NULL OR :createDateTo IS NOT NULL AND res.createDate <= :createDateTo)
                )
                AND (
                    (:shipmentDateFrom IS NULL OR :shipmentDateFrom IS NOT NULL AND res.shipmentDate >= :shipmentDateFrom)
                    AND (:shipmentDateTo IS NULL OR :shipmentDateTo IS NOT NULL AND res.shipmentDate <= :shipmentDateTo)
                )
                AND (:contractNumber IS NULL OR :contractNumber IS NOT NULL AND res.contractNumber = :contractNumber)
                AND (:contractYear IS NULL OR :contractYear IS NOT NULL AND res.sectionYear = :contractYear)
                AND (:contractExternalNumber IS NULL OR :contractExternalNumber IS NOT NULL AND res.externalNumber LIKE :contractExternalNumber)
                AND (:account IS NULL OR :account IS NOT NULL AND res.account LIKE :account)
                AND (:payerId IS NULL OR :payerId IS NOT NULL AND res.payerId = :payerId)
                AND (:consigneeId IS NULL OR :consigneeId IS NOT NULL AND res.consigneeId = :consigneeId)
            GROUP BY
                res.id,
                res.number,
                res.createDate,
                res.shipmentDate,
                res.externalNumber,
                res.contractNumber,
                res.contractPerformer,
                res.contractType,
                res.sectionYear,
                res.sectionNumber,
                res.account,
                res.payer,
                res.consignee,
                res.giveUserLastName,
                res.giveUserFirstName,
                res.giveUserMiddleName,
                res.permitUserLastName,
                res.permitUserFirstName,
                res.permitUserMiddleName,
                res.accountantUserLastName,
                res.accountantUserFirstName,
                res.accountantUserMiddleName,
                res.transmittalLetter,
                res.receiver,
                res.letterOfAttorney,
                res.comment
            ${querySort.queryString(true)}
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.PRELIMINARY_PRICE_KIND, PriceKindType.PRELIMINARY.id)
            .setParameter(ObjAttr.EXPORT_PRICE_KIND, PriceKindType.EXPORT.id)
            .setParameter(ObjAttr.FINAL_PRICE_KIND, PriceKindType.FINAL.id)
            .setParameter(ObjAttr.PRODUCT_NAME, form.string(ObjAttr.PRODUCT_NAME).nullIfBlankOr { "%${it}%" })
            .setParameter(ObjAttr.SERIAL_NUMBER, form.string(ObjAttr.SERIAL_NUMBER).nullIfBlankOr { "%${it}%" })
            .setParameter(ObjAttr.NUMBER, form.int(ObjAttr.NUMBER))
            .setParameter(ObjAttr.CREATE_DATE_FROM, form.date(ObjAttr.CREATE_DATE_FROM)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.CREATE_DATE_TO, form.date(ObjAttr.CREATE_DATE_TO)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.SHIPMENT_DATE_FROM, form.date(ObjAttr.SHIPMENT_DATE_FROM)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.SHIPMENT_DATE_TO, form.date(ObjAttr.SHIPMENT_DATE_TO)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.CONTRACT_NUMBER, form.int(ObjAttr.CONTRACT_NUMBER))
            .setParameter(ObjAttr.CONTRACT_YEAR, form.int(ObjAttr.CONTRACT_YEAR))
            .setParameter(ObjAttr.CONTRACT_EXTERNAL_NUMBER, form.string(ObjAttr.CONTRACT_EXTERNAL_NUMBER).nullIfBlankOr { "%${it}%" })
            .setParameter(ObjAttr.ACCOUNT, form.string(ObjAttr.ACCOUNT).nullIfBlankOr { "%${it}%" })
            .setParameter(ObjAttr.PAYER_ID, form.long(ObjAttr.PAYER_ID))
            .setParameter(ObjAttr.CONSIGNEE_ID, form.long(ObjAttr.CONSIGNEE_ID))
        nativeQuery.firstResult = input.start
        nativeQuery.maxResults = input.size
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.NUMBER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.CREATE_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.SHIPMENT_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.EXTERNAL_NUMBER, StringType.INSTANCE)
            .addScalar(ObjAttr.CONTRACT_NUMBER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.CONTRACT_PERFORMER, LongType.INSTANCE)
            .addScalar(ObjAttr.CONTRACT_TYPE, LongType.INSTANCE)
            .addScalar(ObjAttr.SECTION_YEAR, IntegerType.INSTANCE)
            .addScalar(ObjAttr.SECTION_NUMBER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.ACCOUNT, StringType.INSTANCE)
            .addScalar(ObjAttr.PAYER, StringType.INSTANCE)
            .addScalar(ObjAttr.CONSIGNEE, StringType.INSTANCE)
            .addScalar(ObjAttr.VAT, StringType.INSTANCE)
            .addScalar(ObjAttr.TOTAL_WO_VAT, DoubleType.INSTANCE)
            .addScalar(ObjAttr.TOTAL_VAT, DoubleType.INSTANCE)
            .addScalar(ObjAttr.GIVE_USER_LAST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.GIVE_USER_FIRST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.GIVE_USER_MIDDLE_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.PERMIT_USER_LAST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.PERMIT_USER_FIRST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.PERMIT_USER_MIDDLE_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.ACCOUNTANT_USER_LAST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.ACCOUNTANT_USER_FIRST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.ACCOUNTANT_USER_MIDDLE_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.TRANSMITTAL_LETTER, StringType.INSTANCE)
            .addScalar(ObjAttr.RECEIVER, StringType.INSTANCE)
            .addScalar(ObjAttr.LETTER_OF_ATTORNEY, StringType.INSTANCE)
            .addScalar(ObjAttr.COMMENT, StringType.INSTANCE)
            .addScalar(ROW_COUNT_ALIAS, LongType.INSTANCE)
            .resultTransform(ShipmentWaybillListDto::class)
        return TabrResultQuery.instance(nativeQuery.typedManyResult())
    }

    override fun getLastByYear(year: Int): ShipmentWaybill? {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        return em.createQuery(cq.select(root).where(cb.equal(root.get<Int>(ShipmentWaybill::year.name), year))
            .orderBy(cb.desc(root.get<Int>(ShipmentWaybill::number.name))))
            .setMaxResults(BaseConstant.ONE_INT).typedSingleResult()
    }
}