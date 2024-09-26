package ru.korundm.dao

import org.hibernate.query.NativeQuery
import org.hibernate.type.DoubleType
import org.hibernate.type.LocalDateType
import org.hibernate.type.LongType
import org.hibernate.type.StringType
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant.ONE_LONG
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.constant.BaseConstant.ROW_COUNT_ALIAS
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.Product
import ru.korundm.entity.ProductDeciphermentPeriod
import ru.korundm.entity.ProductPricePeriod
import ru.korundm.helper.*
import ru.korundm.helper.TabrResultQuery.Companion.instance
import ru.korundm.repository.ProductPricePeriodRepository
import ru.korundm.util.KtCommonUtil.ifNotBlank
import ru.korundm.util.KtCommonUtil.resultTransform
import ru.korundm.util.KtCommonUtil.typedManyResult
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.KClass

interface ProductPricePeriodService : CommonService<ProductPricePeriod> {

    fun findTableData(tableInput: TabrIn, form: DynamicObject): TabrResultQuery<ProductPricePeriod>
    fun findProductPeriodTableData(tableInput: TabrIn, form: DynamicObject, productId: Long): TabrResultQuery<ProductPricePeriod>
    fun <T : RowCountable> findPeriodProductTableData(
        tableInput: TabrIn,
        form: DynamicObject,
        pricePeriodId: Long,
        transformClass: KClass<T>
    ): TabrResultQuery<T>
}

@Service
@Transactional
class ProductPricePeriodServiceImpl(
    private val repository: ProductPricePeriodRepository
) : ProductPricePeriodService {

    private val cl = ProductPricePeriod::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductPricePeriod> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductPricePeriod> = repository.findAllById(idList)

    override fun save(obj: ProductPricePeriod): ProductPricePeriod {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductPricePeriod>): List<ProductPricePeriod> = repository.saveAll(objectList)

    override fun read(id: Long): ProductPricePeriod? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductPricePeriod) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(tableInput: TabrIn, form: DynamicObject): TabrResultQuery<ProductPricePeriod> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        val predicateList = mutableListOf<Predicate>()
        form.string(ObjAttr.PERIOD_NAME)?.let { predicateList += cb.like(root.get(ProductPricePeriod::name.name), "%$it%") }
        form.string(ObjAttr.COMMENT)?.let { predicateList += cb.like(root.get(ProductPricePeriod::comment.name), "%$it%") }
        val pathStartDate = root.get<LocalDate>(ProductPricePeriod::startDate.name)
        form.date(ObjAttr.START_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathStartDate, it) }
        form.date(ObjAttr.START_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathStartDate, it) }
        val productName = form.stringNotNull(ObjAttr.PRODUCT_NAME)
        if (productName.isNotBlank()) {
            val subQuery = cq.subquery(Long::class.java)
            val subRoot = subQuery.from(ProductDeciphermentPeriod::class.java)
            subQuery.select(cb.literal(ONE_LONG)).where(
                cb.equal(subRoot.get<ProductPricePeriod>(ProductDeciphermentPeriod::pricePeriod.name), root.get<Long>(ObjAttr.ID)),
                cb.like(subRoot.get<Product>(ProductDeciphermentPeriod::product.name).get(Product::conditionalName.name), "%$productName%")
            )
            predicateList += cb.exists(subQuery)
        }

        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.NAME -> prSort(root.get<String>(ProductPricePeriod::name.name))
                ObjAttr.START_DATE -> prSort(root.get<LocalDate>(ProductPricePeriod::startDate.name))
                ObjAttr.COMMENT -> prSort(root.get<String>(ProductPricePeriod::comment.name))
                else -> prSort(root.get<Long>(ProductPricePeriod::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = tableInput.start
        typedQuery.maxResults = tableInput.size
        return instance(typedQuery.resultList)
    }

    override fun findProductPeriodTableData(tableInput: TabrIn, form: DynamicObject, productId: Long): TabrResultQuery<ProductPricePeriod> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        val predicateList = mutableListOf<Predicate>()
        form.string(ObjAttr.PERIOD_NAME)?.let { predicateList += cb.like(root.get(ProductPricePeriod::name.name), "%$it%") }
        form.string(ObjAttr.COMMENT)?.let { predicateList += cb.like(root.get(ProductPricePeriod::comment.name), "%$it%") }
        val pathStartDate = root.get<LocalDate>(ProductPricePeriod::startDate.name)
        form.date(ObjAttr.START_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathStartDate, it) }
        form.date(ObjAttr.START_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathStartDate, it) }

        val subQuery = cq.subquery(Long::class.java)
        val subRoot = subQuery.from(ProductDeciphermentPeriod::class.java)
        subQuery.select(cb.literal(ONE_LONG)).where(
            cb.equal(subRoot.get<ProductPricePeriod>(ProductDeciphermentPeriod::pricePeriod.name), root.get<Long>(ObjAttr.ID)),
            cb.equal(subRoot.get<Product>(ProductDeciphermentPeriod::product.name), productId)
        )
        predicateList += cb.exists(subQuery).not()

        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.NAME -> prSort(root.get<String>(ProductPricePeriod::name.name))
                ObjAttr.START_DATE -> prSort(root.get<LocalDate>(ProductPricePeriod::startDate.name))
                ObjAttr.COMMENT -> prSort(root.get<String>(ProductPricePeriod::comment.name))
                else -> prSort(root.get<Long>(ProductPricePeriod::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = tableInput.start
        typedQuery.maxResults = tableInput.size
        return instance(typedQuery.resultList)
    }

    override fun <T : RowCountable> findPeriodProductTableData(
        tableInput: TabrIn,
        form: DynamicObject,
        pricePeriodId: Long,
        transformClass: KClass<T>
    ): TabrResultQuery<T> {
        val querySort = QuerySort()
        tableInput.sorter?.let { querySort[when (it.field) {
            ObjAttr.NAME -> "p.conditional_name"
            else -> "p.id"
        }] = it.dir }
        val query = """
            SELECT
                p.id AS ${ObjAttr.ID},
                pdp.id AS ${ObjAttr.PERIOD_ID},
                p.conditional_name AS ${ObjAttr.NAME},
                ppp.start_date AS ${ObjAttr.START_DATE},
                pdp.end_date AS ${ObjAttr.END_DATE},
                pdp.price_pack AS ${ObjAttr.PRICE_PACK},
                pdp.price_wo_pack AS ${ObjAttr.PRICE_WO_PACK},
                pdp.price_pack_research AS ${ObjAttr.PRICE_PACK_RESEARCH},
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM
                product_price_period ppp
                JOIN
                product_decipherment_period pdp
                ON
                pdp.price_period_id = ppp.id
                AND ppp.id = :periodId
                JOIN
                products p ON pdp.product_id = p.id
            WHERE
                :productName = '' OR :productName <> '' AND p.conditional_name LIKE :productName
            ${querySort.queryString(true)}
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.PRODUCT_NAME, form.stringNotNull(ObjAttr.PRODUCT_NAME).ifNotBlank { "%${it}%" })
            .setParameter(ObjAttr.PERIOD_ID, pricePeriodId)
        nativeQuery.firstResult = tableInput.start
        nativeQuery.maxResults = tableInput.size
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.PERIOD_ID, LongType.INSTANCE)
            .addScalar(ObjAttr.NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.START_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.END_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.PRICE_PACK, DoubleType.INSTANCE)
            .addScalar(ObjAttr.PRICE_WO_PACK, DoubleType.INSTANCE)
            .addScalar(ObjAttr.PRICE_PACK_RESEARCH, DoubleType.INSTANCE)
            .addScalar(ROW_COUNT_ALIAS, LongType.INSTANCE)
            .resultTransform(transformClass)
        return instance(nativeQuery.typedManyResult())
    }
}