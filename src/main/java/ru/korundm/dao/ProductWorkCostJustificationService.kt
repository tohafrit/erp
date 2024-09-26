package ru.korundm.dao

import org.hibernate.query.NativeQuery
import org.hibernate.type.LocalDateType
import org.hibernate.type.LongType
import org.hibernate.type.StringType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.constant.BaseConstant.ROW_COUNT_ALIAS
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.ProductWorkCostJustification
import ru.korundm.helper.*
import ru.korundm.repository.ProductWorkCostJustificationRepository
import ru.korundm.util.KtCommonUtil.resultTransform
import ru.korundm.util.KtCommonUtil.typedManyResult
import java.sql.Date
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.TemporalType
import kotlin.reflect.KClass

interface ProductWorkCostJustificationService : CommonService<ProductWorkCostJustification> {

    fun <T : RowCountable> findTableData(
        tableInput: TabrIn,
        form: DynamicObject,
        transformClass: KClass<T>
    ): TabrResultQuery<T>
    fun getLastApproved(): ProductWorkCostJustification?
}

@Service
@Transactional
class ProductWorkCostJustificationServiceImpl(
    private val repository: ProductWorkCostJustificationRepository
) : ProductWorkCostJustificationService {

    private val cl = ProductWorkCostJustification::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductWorkCostJustification> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductWorkCostJustification> = repository.findAllById(idList)

    override fun save(obj: ProductWorkCostJustification): ProductWorkCostJustification {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductWorkCostJustification>): List<ProductWorkCostJustification> = repository.saveAll(objectList)

    override fun read(id: Long): ProductWorkCostJustification? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductWorkCostJustification) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun <T : RowCountable> findTableData(
        tableInput: TabrIn,
        form: DynamicObject,
        transformClass: KClass<T>
    ): TabrResultQuery<T> {
        val querySort = QuerySort()
        tableInput.sorter?.let { querySort[when (it.field) {
            ObjAttr.NAME -> "pwcj.approval_date"
            ObjAttr.COMMENT -> "pwcj.comment"
            else -> "pwcj.id"
        }] = it.dir }
        val query = """
            SELECT
                pwcj.id AS ${ObjAttr.ID},
                pwcj.name AS ${ObjAttr.NAME},
                pwcj.approval_date AS ${ObjAttr.APPROVAL_DATE},
                pwcj.create_date AS ${ObjAttr.CREATE_DATE},
                pwcj.comment AS ${ObjAttr.COMMENT},
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM
                product_work_cost_justification pwcj
            WHERE
                (:approvalDateFrom IS NULL OR :approvalDateFrom IS NOT NULL AND pwcj.approval_date >= :approvalDateFrom)
                AND (:approvalDateTo IS NULL OR :approvalDateTo IS NOT NULL AND pwcj.approval_date <= :approvalDateTo)
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

    override fun getLastApproved() = repository.findFirstByApprovalDateIsNotNullOrderByApprovalDateDesc()
}