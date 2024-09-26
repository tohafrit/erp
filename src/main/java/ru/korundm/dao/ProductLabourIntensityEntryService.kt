package ru.korundm.dao

import org.hibernate.query.NativeQuery
import org.hibernate.type.DoubleType
import org.hibernate.type.LocalDateType
import org.hibernate.type.LongType
import org.hibernate.type.StringType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.constant.BaseConstant.ROW_COUNT_ALIAS
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.ProductLabourIntensityEntry
import ru.korundm.helper.*
import ru.korundm.repository.ProductLabourIntensityEntryRepository
import ru.korundm.util.KtCommonUtil.ifNotBlank
import ru.korundm.util.KtCommonUtil.resultTransform
import ru.korundm.util.KtCommonUtil.typedManyResult
import java.sql.Date
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.TemporalType
import kotlin.reflect.KClass

interface ProductLabourIntensityEntryService : CommonService<ProductLabourIntensityEntry> {

    fun <T : RowCountable> findTableData(
        tableInput: TabrIn,
        form: DynamicObject,
        labourIntensityId: Long,
        transformClass: KClass<T>
    ): TabrResultQuery<T>
    fun getByLabourIntensityIdAndProductId(labourIntensityId: Long?, productId: Long?): ProductLabourIntensityEntry?
    fun deleteAll(list: List<ProductLabourIntensityEntry>)
    fun isApprovedByProductId(productId: Long?): Boolean
    fun deleteAllByJustificationId(id: Long?)
    fun isApprovedByJustificationId(id: Long?): Boolean
    fun deleteAllByIdList(idList: List<Long>)
}

@Service
@Transactional
class ProductLabourIntensityEntryServiceImpl(
    private val repository: ProductLabourIntensityEntryRepository
) : ProductLabourIntensityEntryService {

    private val cl = ProductLabourIntensityEntry::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductLabourIntensityEntry> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductLabourIntensityEntry> = repository.findAllById(idList)

    override fun save(obj: ProductLabourIntensityEntry): ProductLabourIntensityEntry {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductLabourIntensityEntry>): List<ProductLabourIntensityEntry> = repository.saveAll(objectList)

    override fun read(id: Long): ProductLabourIntensityEntry? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductLabourIntensityEntry) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun <T : RowCountable> findTableData(
        tableInput: TabrIn,
        form: DynamicObject,
        labourIntensityId: Long,
        transformClass: KClass<T>
    ): TabrResultQuery<T> {
        val querySort = QuerySort()
        val sortFields = arrayOf(ObjAttr.PRODUCT_NAME, ObjAttr.DECIMAL_NUMBER, ObjAttr.TOTAL_LABOUR_INTENSITY, ObjAttr.CREATE_DATE, ObjAttr.APPROVAL_DATE)
        tableInput.sorter?.let { querySort[if (sortFields.contains(it.field)) it.field else "plie.id"] = it.dir }
        val query = """
            SELECT
                plie.id AS ${ObjAttr.ID},
                p.conditional_name AS ${ObjAttr.PRODUCT_NAME},
                p.decimal_number AS ${ObjAttr.DECIMAL_NUMBER},
                SUM(IFNULL(plio.value, 0)) AS ${ObjAttr.TOTAL_LABOUR_INTENSITY},
                plie.create_date AS ${ObjAttr.CREATE_DATE},
                u1.last_name AS ${ObjAttr.CREATOR_LAST_NAME},
                u1.first_name AS ${ObjAttr.CREATOR_FIRST_NAME},
                u1.middle_name AS ${ObjAttr.CREATOR_MIDDLE_NAME},
                plie.approval_date AS ${ObjAttr.APPROVAL_DATE},
                u2.last_name AS ${ObjAttr.APPROVER_LAST_NAME},
                u2.first_name AS ${ObjAttr.APPROVER_FIRST_NAME},
                u2.middle_name AS ${ObjAttr.APPROVER_MIDDLE_NAME},
                plie.comment AS ${ObjAttr.COMMENT},
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM
                product_labour_intensity_entry plie
                LEFT JOIN
                product_labour_intensity_operation plio ON plie.id = plio.entry_id
                JOIN
                products p ON plie.product_id = p.id
                JOIN
                users u1 ON plie.created_by = u1.id
                LEFT JOIN
                users u2 ON plie.approved_by = u2.id
            WHERE
                plie.labour_intensity_id = :labourIntensityId
                AND (:productName = '' OR :productName <> '' AND p.conditional_name LIKE :productName)
                AND (:decimalNumber = '' OR :decimalNumber <> '' AND p.decimal_number LIKE :decimalNumber)
                AND (
                    (:createDateFrom IS NULL OR :createDateFrom IS NOT NULL AND plie.create_date >= :createDateFrom)
                    AND (:createDateTo IS NULL OR :createDateTo IS NOT NULL AND plie.create_date <= :createDateTo)
                )
                AND (
                    (:approvalDateFrom IS NULL OR :approvalDateFrom IS NOT NULL AND plie.approval_date >= :approvalDateFrom)
                    AND (:approvalDateTo IS NULL OR :approvalDateTo IS NOT NULL AND plie.approval_date <= :approvalDateTo)
                )
            GROUP BY
                plie.id,
                p.conditional_name,
                p.decimal_number,
                plie.create_date,
                u1.last_name,
                u1.first_name,
                u1.middle_name,
                plie.approval_date,
                u2.last_name,
                u2.first_name,
                u2.middle_name,
                plie.comment
            ${querySort.queryString(true)}
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.LABOUR_INTENSITY_ID, labourIntensityId)
            .setParameter(ObjAttr.PRODUCT_NAME, form.stringNotNull(ObjAttr.PRODUCT_NAME).ifNotBlank { "%${it}%" })
            .setParameter(ObjAttr.DECIMAL_NUMBER, form.stringNotNull(ObjAttr.DECIMAL_NUMBER).ifNotBlank { "%${it}%" })
            .setParameter(ObjAttr.CREATE_DATE_FROM, form.date(ObjAttr.CREATE_DATE_FROM)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.CREATE_DATE_TO, form.date(ObjAttr.CREATE_DATE_TO)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.APPROVAL_DATE_FROM, form.date(ObjAttr.APPROVAL_DATE_FROM)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.APPROVAL_DATE_TO, form.date(ObjAttr.APPROVAL_DATE_TO)?.let { Date.valueOf(it) }, TemporalType.DATE)
        nativeQuery.firstResult = tableInput.start
        nativeQuery.maxResults = tableInput.size
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.PRODUCT_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.DECIMAL_NUMBER, StringType.INSTANCE)
            .addScalar(ObjAttr.TOTAL_LABOUR_INTENSITY, DoubleType.INSTANCE)
            .addScalar(ObjAttr.CREATE_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.CREATOR_LAST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.CREATOR_FIRST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.CREATOR_MIDDLE_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.APPROVAL_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.APPROVER_LAST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.APPROVER_FIRST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.APPROVER_MIDDLE_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.COMMENT, StringType.INSTANCE)
            .addScalar(ROW_COUNT_ALIAS, LongType.INSTANCE)
            .resultTransform(transformClass)
        return TabrResultQuery.instance(nativeQuery.typedManyResult())
    }

    override fun getByLabourIntensityIdAndProductId(labourIntensityId: Long?, productId: Long?) =
        if (labourIntensityId == null || productId == null) null else repository.findFirstByLabourIntensityIdAndProductId(labourIntensityId, productId)

    override fun deleteAll(list: List<ProductLabourIntensityEntry>) = repository.deleteAll(list)

    override fun isApprovedByProductId(productId: Long?) = if (productId == null) false else repository.existsByProductIdAndApprovalDateIsNotNull(productId)

    override fun deleteAllByJustificationId(id: Long?) = if (id == null) Unit else repository.deleteAllByLabourIntensityId(id)

    override fun isApprovedByJustificationId(id: Long?) = if (id == null) false else repository.existsByLabourIntensityIdAndApprovalDateIsNotNull(id)

    override fun deleteAllByIdList(idList: List<Long>) = repository.deleteAllByIdIn(idList)
}