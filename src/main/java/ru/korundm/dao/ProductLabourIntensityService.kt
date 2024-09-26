package ru.korundm.dao

import org.hibernate.query.NativeQuery
import org.hibernate.type.IntegerType
import org.hibernate.type.LocalDateType
import org.hibernate.type.LongType
import org.hibernate.type.StringType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.constant.BaseConstant.ROW_COUNT_ALIAS
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.ProductLabourIntensity
import ru.korundm.helper.*
import ru.korundm.repository.ProductLabourIntensityRepository
import ru.korundm.util.KtCommonUtil.ifNotBlank
import ru.korundm.util.KtCommonUtil.resultTransform
import ru.korundm.util.KtCommonUtil.typedManyResult
import ru.korundm.util.KtCommonUtil.typedSingleResult
import java.sql.Date
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.TemporalType
import kotlin.reflect.KClass

interface ProductLabourIntensityService : CommonService<ProductLabourIntensity> {

    fun <T : Any> findDataById(id: Long, transformClass: KClass<T>): T?
    fun <T : RowCountable> findTableData(
        tableInput: TabrIn,
        form: DynamicObject,
        transformClass: KClass<T>
    ): TabrResultQuery<T>
    fun getLastApprovedByProductId(productId: Long?): ProductLabourIntensity?
}

@Service
@Transactional
class ProductLabourIntensityServiceImpl(
    private val repository: ProductLabourIntensityRepository
) : ProductLabourIntensityService {

    private val cl = ProductLabourIntensity::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductLabourIntensity> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductLabourIntensity> = repository.findAllById(idList)

    override fun save(obj: ProductLabourIntensity): ProductLabourIntensity {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductLabourIntensity>): List<ProductLabourIntensity> = repository.saveAll(objectList)

    override fun read(id: Long): ProductLabourIntensity? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductLabourIntensity) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun <T : Any> findDataById(id: Long, transformClass: KClass<T>): T? {
        val query = """
            SELECT
                pli.id AS ${ObjAttr.ID},
                pli.version AS ${ObjAttr.VERSION},
                pli.name AS ${ObjAttr.NAME},
                pli.comment AS ${ObjAttr.COMMENT},
                pli.create_date AS ${ObjAttr.CREATE_DATE},
                u.last_name AS ${ObjAttr.LAST_NAME},
                u.first_name AS ${ObjAttr.FIRST_NAME},
                u.middle_name AS ${ObjAttr.MIDDLE_NAME},
                SUM(IF(plie.approval_date IS NULL, 0, 1)) AS ${ObjAttr.APPROVED_COUNT},
                COUNT(plie.id) AS ${ObjAttr.TOTAL_COUNT}
            FROM
                product_labour_intensity pli
                LEFT JOIN
                product_labour_intensity_entry plie
                ON pli.id = plie.labour_intensity_id
                JOIN
                users u
                ON u.id = pli.created_by
            WHERE
                pli.id = :id
            GROUP BY
                pli.id,
                pli.version,
                pli.name,
                pli.comment,
                pli.create_date,
                u.last_name,
                u.first_name,
                u.middle_name
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query).setParameter(ObjAttr.ID, id)
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.VERSION, LongType.INSTANCE)
            .addScalar(ObjAttr.NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.COMMENT, StringType.INSTANCE)
            .addScalar(ObjAttr.CREATE_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.LAST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.FIRST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.MIDDLE_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.APPROVED_COUNT, IntegerType.INSTANCE)
            .addScalar(ObjAttr.TOTAL_COUNT, IntegerType.INSTANCE)
            .resultTransform(transformClass)
        return nativeQuery.typedSingleResult()
    }

    override fun <T : RowCountable> findTableData(
        tableInput: TabrIn,
        form: DynamicObject,
        transformClass: KClass<T>
    ): TabrResultQuery<T> {
        val querySort = QuerySort()
        tableInput.sorter?.let { querySort[when (it.field) {
            ObjAttr.NAME -> "pli.name"
            ObjAttr.CREATE_DATE -> "pli.create_date"
            else -> "pli.id"
        }] = it.dir }
        val query = """
            SELECT
                pli.id AS ${ObjAttr.ID},
                pli.name AS ${ObjAttr.NAME},
                pli.comment AS ${ObjAttr.COMMENT},
                pli.create_date AS ${ObjAttr.CREATE_DATE},
                u.last_name AS ${ObjAttr.LAST_NAME},
                u.first_name AS ${ObjAttr.FIRST_NAME},
                u.middle_name AS ${ObjAttr.MIDDLE_NAME},
                SUM(IF(plie.approval_date IS NULL, 0, 1)) AS ${ObjAttr.APPROVED_COUNT},
                COUNT(plie.id) AS ${ObjAttr.TOTAL_COUNT},
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM
                product_labour_intensity pli
                LEFT JOIN
                product_labour_intensity_entry plie
                ON pli.id = plie.labour_intensity_id
                JOIN
                users u
                ON u.id = pli.created_by
            WHERE
                (:name = '' OR :name <> '' AND pli.name LIKE :name)
                AND (
                    (:createDateFrom IS NULL OR :createDateFrom IS NOT NULL AND pli.create_date >= :createDateFrom)
                    AND (:createDateTo IS NULL OR :createDateTo IS NOT NULL AND pli.create_date <= :createDateTo)
                )
                AND (:productName = '' OR :productName <> '' AND EXISTS (
                    SELECT
                        1
                    FROM
                        products p1
                        JOIN
                        product_labour_intensity_entry plie1
                        ON
                        p1.id = plie1.product_id
                        AND p1.conditional_name LIKE :productName
                    WHERE pli.id = plie1.labour_intensity_id
                ))
                AND (:productId IS NULL OR :productId IS NOT NULL AND EXISTS (
                    SELECT
                        1
                    FROM
                        products p1
                        JOIN
                        product_labour_intensity_entry plie1
                        ON
                        p1.id = plie1.product_id
                        AND p1.id = :productId
                    WHERE pli.id = plie1.labour_intensity_id
                ))
                AND (:isProductApproved = 0 OR :isProductApproved = 1 AND EXISTS (
                    SELECT
                        1
                    FROM
                        product_labour_intensity_entry plie1
                    WHERE 
                        pli.id = plie1.labour_intensity_id
                        AND plie1.approval_date IS NOT NULL
                ))
            GROUP BY
                pli.id,
                pli.name,
                pli.comment,
                pli.create_date,
                u.last_name,
                u.first_name,
                u.middle_name
            ${querySort.queryString(true)}
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.NAME, form.stringNotNull(ObjAttr.NAME).ifNotBlank { "%${it}%" })
            .setParameter(ObjAttr.PRODUCT_NAME, form.stringNotNull(ObjAttr.PRODUCT_NAME).ifNotBlank { "%${it}%" })
            .setParameter(ObjAttr.PRODUCT_ID, form.long(ObjAttr.PRODUCT_ID))
            .setParameter(ObjAttr.IS_PRODUCT_APPROVED, form.boolNotNull(ObjAttr.IS_PRODUCT_APPROVED))
            .setParameter(ObjAttr.CREATE_DATE_FROM, form.date(ObjAttr.CREATE_DATE_FROM)?.let { Date.valueOf(it) }, TemporalType.DATE)
            .setParameter(ObjAttr.CREATE_DATE_TO, form.date(ObjAttr.CREATE_DATE_TO)?.let { Date.valueOf(it) }, TemporalType.DATE)
        nativeQuery.firstResult = tableInput.start
        nativeQuery.maxResults = tableInput.size
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.COMMENT, StringType.INSTANCE)
            .addScalar(ObjAttr.CREATE_DATE, LocalDateType.INSTANCE)
            .addScalar(ObjAttr.LAST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.FIRST_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.MIDDLE_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.APPROVED_COUNT, IntegerType.INSTANCE)
            .addScalar(ObjAttr.TOTAL_COUNT, IntegerType.INSTANCE)
            .addScalar(ROW_COUNT_ALIAS, LongType.INSTANCE)
            .resultTransform(transformClass)
        return TabrResultQuery.instance(nativeQuery.typedManyResult())
    }

    override fun getLastApprovedByProductId(productId: Long?) = if (productId == null) null else repository.getFirstByEntryListProductIdOrderByEntryListApprovalDateDesc(productId)
}