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
import ru.korundm.entity.ProductDeciphermentPeriod
import ru.korundm.helper.QuerySort
import ru.korundm.helper.RowCountable
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.ProductDeciphermentPeriodRepository
import ru.korundm.util.KtCommonUtil.resultTransform
import ru.korundm.util.KtCommonUtil.typedManyResult
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import kotlin.reflect.KClass

interface ProductDeciphermentPeriodService : CommonService<ProductDeciphermentPeriod> {

    fun getLastByProductId(productId: Long?): ProductDeciphermentPeriod?
    fun existsByProductIdAndPricePeriodId(productId: Long?, pricePeriodId: Long?): Boolean
    fun <T : RowCountable> findProductPeriodTableData(
        tableInput: TabrIn,
        productId: Long,
        transformClass: KClass<T>
    ): TabrResultQuery<T>
    fun existsByPricePeriod(pricePeriodId: Long?): Boolean
    fun <T : RowCountable> findReportPeriodTableData(
        input: TabrIn,
        productId: Long,
        planPeriodId: Long,
        excludePeriodId: Long?,
        transformCl: KClass<T>
    ): TabrResultQuery<T>
}

@Service
@Transactional
class ProductDeciphermentPeriodServiceImpl(
    private val repository: ProductDeciphermentPeriodRepository
) : ProductDeciphermentPeriodService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductDeciphermentPeriod> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductDeciphermentPeriod> = repository.findAllById(idList)

    override fun save(obj: ProductDeciphermentPeriod): ProductDeciphermentPeriod {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductDeciphermentPeriod>): List<ProductDeciphermentPeriod> = repository.saveAll(objectList)

    override fun read(id: Long): ProductDeciphermentPeriod? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductDeciphermentPeriod) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun <T : RowCountable> findProductPeriodTableData(
        tableInput: TabrIn,
        productId: Long,
        transformClass: KClass<T>
    ): TabrResultQuery<T> {
        val querySort = QuerySort()
        tableInput.sorter?.let { querySort[when (it.field) {
            ObjAttr.PLAN_NAME -> "ppp.name"
            ObjAttr.PLAN_START_DATE -> "ppp.start_date"
            ObjAttr.REPORT_NAME -> "rppp.name"
            ObjAttr.REPORT_START_DATE -> "rppp.start_date"
            else -> "pdp.id"
        }] = it.dir }
        val query = """
            SELECT
                pdp.id AS ${ObjAttr.ID},
                ppp.name AS ${ObjAttr.PLAN_NAME},
                ppp.start_date AS ${ObjAttr.PLAN_START_DATE},
                ppp.comment AS ${ObjAttr.PLAN_COMMENT},
                rppp.name AS ${ObjAttr.REPORT_NAME},
                rppp.start_date AS ${ObjAttr.REPORT_START_DATE},
                rppp.comment AS ${ObjAttr.REPORT_COMMENT},
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM
                product_decipherment_period pdp
                JOIN
                product_price_period ppp
                ON
                pdp.price_period_id = ppp.id
                AND pdp.product_id = :productId
                LEFT JOIN
                product_decipherment_period rpdp
                ON rpdp.id = pdp.prev_period_id
                LEFT JOIN
                product_price_period rppp
                ON rpdp.price_period_id = rppp.id
            ${querySort.queryString(true)}
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query).setParameter(ObjAttr.PRODUCT_ID, productId)
        nativeQuery.firstResult = tableInput.start
        nativeQuery.maxResults = tableInput.size
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.PLAN_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.PLAN_START_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.PLAN_COMMENT, StringType.INSTANCE)
            .addScalar(ObjAttr.REPORT_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.REPORT_START_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.REPORT_COMMENT, StringType.INSTANCE)
            .addScalar(ROW_COUNT_ALIAS, LongType.INSTANCE)
            .resultTransform(transformClass)
        return TabrResultQuery.instance(nativeQuery.typedManyResult())
    }

    override fun existsByPricePeriod(pricePeriodId: Long?) = if (pricePeriodId == null) false else repository.existsByPricePeriodId(pricePeriodId)

    override fun <T : RowCountable> findReportPeriodTableData(
        input: TabrIn,
        productId: Long,
        planPeriodId: Long,
        excludePeriodId: Long?,
        transformCl: KClass<T>
    ): TabrResultQuery<T> {
        val querySort = QuerySort()
        input.sorter?.let { querySort[when (it.field) {
            ObjAttr.NAME -> "ppp.name"
            ObjAttr.START_DATE -> "ppp.start_date"
            else -> "pdp.id"
        }] = it.dir }
        val query = """
            SELECT
                pdp.id AS ${ObjAttr.ID},
                ppp.name AS ${ObjAttr.NAME},
                ppp.start_date AS ${ObjAttr.START_DATE},
                ppp.comment AS ${ObjAttr.COMMENT},
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM
                product_decipherment_period pdp
                JOIN
                product_price_period ppp
                ON
                pdp.price_period_id = ppp.id
                AND pdp.product_id = :productId
                AND pdp.id <> :excludePeriodId
                AND pdp.end_date IS NULL
            WHERE
                EXISTS (
                    SELECT 1
                    FROM product_price_period sppp
                    WHERE
                        sppp.id = :planPeriodId
                        AND sppp.start_date >= ppp.start_date
                )
            ${querySort.queryString(true)}
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.PRODUCT_ID, productId)
            .setParameter(ObjAttr.PLAN_PERIOD_ID, planPeriodId)
            .setParameter(ObjAttr.EXCLUDE_PERIOD_ID, excludePeriodId ?: 0)
        nativeQuery.firstResult = input.start
        nativeQuery.maxResults = input.size
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.START_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.COMMENT, StringType.INSTANCE)
            .addScalar(ROW_COUNT_ALIAS, LongType.INSTANCE)
            .resultTransform(transformCl)
        return TabrResultQuery.instance(nativeQuery.typedManyResult())
    }

    override fun getLastByProductId(productId: Long?) = if (productId == null) null else repository.findFirstByProductIdOrderByPricePeriodStartDateDesc(productId)

    override fun existsByProductIdAndPricePeriodId(productId: Long?, pricePeriodId: Long?) = if (productId == null || pricePeriodId == null) false else repository.existsByProductIdAndPricePeriodId(productId, pricePeriodId)
}