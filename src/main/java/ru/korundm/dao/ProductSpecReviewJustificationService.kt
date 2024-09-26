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
import ru.korundm.entity.ProductSpecReviewJustification
import ru.korundm.helper.*
import ru.korundm.repository.ProductSpecReviewJustificationRepository
import ru.korundm.util.KtCommonUtil.resultTransform
import ru.korundm.util.KtCommonUtil.typedManyResult
import java.sql.Date
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.TemporalType
import kotlin.reflect.KClass

interface ProductSpecReviewJustificationService : CommonService<ProductSpecReviewJustification> {

    fun <T : RowCountable> findTableData(
        tableInput: TabrIn,
        form: DynamicObject,
        transformClass: KClass<T>
    ): TabrResultQuery<T>
    fun <T : RowCountable> findDeciphermentTableData(
        tableInput: TabrIn,
        form: DynamicObject,
        classGroupId: Long?,
        transformClass: KClass<T>
    ): TabrResultQuery<T>
    fun getLastApproved(): ProductSpecReviewJustification?
}

@Service
@Transactional
class ProductSpecReviewJustificationServiceImpl(
    private val repository: ProductSpecReviewJustificationRepository
) : ProductSpecReviewJustificationService {

    private val cl = ProductSpecReviewJustification::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductSpecReviewJustification> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductSpecReviewJustification> = repository.findAllById(idList)

    override fun save(obj: ProductSpecReviewJustification): ProductSpecReviewJustification {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductSpecReviewJustification>): List<ProductSpecReviewJustification> = repository.saveAll(objectList)

    override fun read(id: Long): ProductSpecReviewJustification? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductSpecReviewJustification) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun <T : RowCountable> findTableData(
        tableInput: TabrIn,
        form: DynamicObject,
        transformClass: KClass<T>
    ): TabrResultQuery<T> {
        val querySort = QuerySort()
        tableInput.sorter?.let { querySort[when (it.field) {
            ObjAttr.NAME -> "psrj.approval_date"
            ObjAttr.COMMENT -> "psrj.comment"
            else -> "psrj.id"
        }] = it.dir }
        val query = """
            SELECT
                psrj.id AS ${ObjAttr.ID},
                psrj.name AS ${ObjAttr.NAME},
                psrj.approval_date AS ${ObjAttr.APPROVAL_DATE},
                psrj.create_date AS ${ObjAttr.CREATE_DATE},
                psrj.comment AS ${ObjAttr.COMMENT},
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM
                product_spec_review_justification psrj
            WHERE
                (:approvalDateFrom IS NULL OR :approvalDateFrom IS NOT NULL AND psrj.approval_date >= :approvalDateFrom)
                AND (:approvalDateTo IS NULL OR :approvalDateTo IS NOT NULL AND psrj.approval_date <= :approvalDateTo)
            ${querySort.queryString(true)}
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.APPROVAL_DATE_FROM, form.date(ObjAttr.APPROVAL_DATE_FROM)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.APPROVAL_DATE_TO, form.date(ObjAttr.APPROVAL_DATE_TO)?.let { Date.valueOf(it) }, TemporalType.DATE)
        nativeQuery.firstResult = tableInput.start
        nativeQuery.maxResults = tableInput.size
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.APPROVAL_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.CREATE_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.COMMENT, StringType.INSTANCE)
            .addScalar(ROW_COUNT_ALIAS, LongType.INSTANCE)
            .resultTransform(transformClass)
        return TabrResultQuery.instance(nativeQuery.typedManyResult())
    }

    override fun <T : RowCountable> findDeciphermentTableData(
        tableInput: TabrIn,
        form: DynamicObject,
        classGroupId: Long?,
        transformClass: KClass<T>
    ): TabrResultQuery<T> {
        val querySort = QuerySort()
        tableInput.sorter?.let { querySort[when (it.field) {
            ObjAttr.NAME -> "psrj.approval_date"
            ObjAttr.COMMENT -> "psrj.comment"
            else -> "psrj.id"
        }] = it.dir }
        val query = """
            SELECT
                psrj.id AS ${ObjAttr.ID},
                psrj.name AS ${ObjAttr.NAME},
                IFNULL(psrp.price, 0) AS ${ObjAttr.PRICE},
                psrj.approval_date AS ${ObjAttr.APPROVAL_DATE},
                psrj.create_date AS ${ObjAttr.CREATE_DATE},
                psrj.comment AS ${ObjAttr.COMMENT},
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM
                product_spec_review_justification psrj
                LEFT JOIN
                product_spec_review_price psrp
                ON psrj.id = psrp.justification_id
                AND psrp.group_id = :classGroupId
            WHERE
                (:approvalDateFrom IS NULL OR :approvalDateFrom IS NOT NULL AND psrj.approval_date >= :approvalDateFrom)
                AND (:approvalDateTo IS NULL OR :approvalDateTo IS NOT NULL AND psrj.approval_date <= :approvalDateTo)
            ${querySort.queryString(true)}
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.CLASS_GROUP_ID, classGroupId ?: 0)
            .setParameter(ObjAttr.APPROVAL_DATE_FROM, form.date(ObjAttr.APPROVAL_DATE_FROM)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.APPROVAL_DATE_TO, form.date(ObjAttr.APPROVAL_DATE_TO)?.let { Date.valueOf(it) }, TemporalType.DATE)
        nativeQuery.firstResult = tableInput.start
        nativeQuery.maxResults = tableInput.size
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.PRICE, DoubleType.INSTANCE)
            .addScalar(ObjAttr.APPROVAL_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.CREATE_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.COMMENT, StringType.INSTANCE)
            .addScalar(ROW_COUNT_ALIAS, LongType.INSTANCE)
            .resultTransform(transformClass)
        return TabrResultQuery.instance(nativeQuery.typedManyResult())
    }

    override fun getLastApproved() = repository.findFirstByApprovalDateIsNotNullOrderByApprovalDateDesc()
}