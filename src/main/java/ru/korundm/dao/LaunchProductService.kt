package ru.korundm.dao

import org.hibernate.query.NativeQuery
import org.hibernate.type.BooleanType
import org.hibernate.type.IntegerType
import org.hibernate.type.LongType
import org.hibernate.type.StringType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.constant.BaseConstant.ROW_COUNT_ALIAS
import ru.korundm.constant.ObjAttr
import ru.korundm.dto.prod.LaunchDetailListDto
import ru.korundm.entity.Launch
import ru.korundm.entity.LaunchProduct
import ru.korundm.entity.LaunchProductStruct
import ru.korundm.entity.Product
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.QuerySort
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.LaunchProductRepository
import ru.korundm.util.KtCommonUtil.nullIfBlankOr
import ru.korundm.util.KtCommonUtil.resultTransform
import ru.korundm.util.KtCommonUtil.typedManyResult
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface LaunchProductService : CommonService<LaunchProduct> {

   fun getAllByVersionIdAndApproveDateIsNull(versionId: Long?): List<LaunchProduct>
   fun getOrAddByLaunchIdAndProductId(launchId: Long?, productId: Long?): LaunchProduct?
   fun getByLaunchIdAndProductId(launchId: Long?, productId: Long?): LaunchProduct?
   fun findDetailTableData(input: TabrIn, launchId: Long, form: DynamicObject): TabrResultQuery<LaunchDetailListDto>
}

@Service
@Transactional
class LaunchProductServiceImpl(
    private val repository: LaunchProductRepository,
    private val bomService: BomService,
    private val bomAttributeService: BomAttributeService,
    private val bomSpecItemService: BomSpecItemService,
    private val launchProductStructService: LaunchProductStructService
) : LaunchProductService {

    companion object {
        const val PRETENDER_TYPE = 1
        const val LAUNCHED_TYPE = 2
    }

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<LaunchProduct> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<LaunchProduct> = repository.findAllById(idList)

    override fun save(obj: LaunchProduct): LaunchProduct {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<LaunchProduct>): List<LaunchProduct> = repository.saveAll(objectList)

    override fun read(id: Long): LaunchProduct? = repository.findById(id).orElse(null)

    override fun delete(obj: LaunchProduct) = throw UnsupportedOperationException("deleting LaunchProduct is prohibited by logic")

    override fun deleteById(id: Long) = throw UnsupportedOperationException("deleting LaunchProduct is prohibited by logic")

    override fun getAllByVersionIdAndApproveDateIsNull(versionId: Long?) = if (versionId == null) emptyList() else repository.findAllByVersionApproveDateIsNullAndVersionId(versionId)

    override fun getOrAddByLaunchIdAndProductId(launchId: Long?, productId: Long?): LaunchProduct? {
        if (launchId == null || productId == null) return null
        var lp = repository.findFirstByLaunchIdAndProductId(launchId, productId)
        if (lp == null) {
            lp = LaunchProduct()
            lp.launch = Launch(launchId)
            lp.product = Product(productId)
            lp.version = bomService.getLastByProductId(productId)
            lp.versionApproveDate = bomAttributeService.getByLaunchIdAndBomId(launchId, lp.version?.id)?.approveDate
            save(lp)
            bomSpecItemService.getAllByBomId(lp.version?.id).forEach {
                val lps = LaunchProductStruct()
                lps.launchProduct = lp
                lps.product = it.product
                lps.amount = it.quantity
                launchProductStructService.save(lps)
            }
        }
        return lp
    }

    override fun getByLaunchIdAndProductId(launchId: Long?, productId: Long?) = if (launchId == null || productId == null) null else repository.findFirstByLaunchIdAndProductId(launchId, productId)

    override fun findDetailTableData(
        input: TabrIn,
        launchId: Long,
        form: DynamicObject
    ): TabrResultQuery<LaunchDetailListDto> {
        val querySort = QuerySort()
        input.sorter?.let { when (it.field) {
            ObjAttr.PRODUCT_NAME -> querySort["p.conditional_name"] = it.dir
            else -> querySort["p.id"] = it.dir
        }}
        val query = """
            SELECT
                p.id AS id,
                lp.id AS lpId,
                p.type_id AS productTypeId,
                IFNULL(pretender.amount, 0) > 0 AS hasPretender,
                IFNULL(lp.contract_amount, 0) > 0 OR IFNULL(lp.rf_contract_amount, 0) > 0 OR IFNULL(lp.rf_assembled_amount, 0) > 0 AS isLaunched, -- + если находится в составе другого запускаемого изделия
                p.conditional_name AS productName,
                IFNULL(lpv.id, plv.v_id) AS verId,
                IFNULL(lpv.major, plv.v_major) AS verMajor,
                IFNULL(lpv.minor, plv.v_minor) AS verMinor,
                IFNULL(lpv.modification, plv.v_modification) AS verMod,
                IFNULL(residueReserveContract.amount, 0) AS residueReserveContract,
                0 AS residueReserve,
                0 AS inStructReserve,
                IFNULL(pretender.amount, 0) AS pretenders,
                IFNULL(lp.contract_amount, 0) AS forContract,
                IFNULL(lp.rf_contract_amount, 0) AS reserveContract,
                IFNULL(lp.rf_assembled_amount, 0) AS reserve,
                IFNULL(lp.contract_amount, 0) + IFNULL(lp.rf_contract_amount, 0) + IFNULL(lp.rf_assembled_amount, 0) AS total,
                0 AS launchInStructOther,
                0 AS totalBeforeUsedReserve,
                IFNULL(lp.ufrf_contract_amount, 0) AS usedReserveContract,
                0 AS inStructUsedReserve,
                0 AS usedReserveAssemble,
                0 AS totalUsedReserve,
                0 AS totalAfterUsedReserve,
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM
                products p
                LEFT JOIN
                launch_product lp
                ON p.id = lp.product_id AND lp.launch_id = :launchId
                -- Версия по изделию в запуске
                LEFT JOIN
                boms lpv
                ON lpv.id = lp.version_id
                -- Последняя версия по изделию
                LEFT JOIN
                v_product_last_version plv
                ON plv.product_id = p.id
                -- Претенденты
                LEFT JOIN (
                    SELECT
                        lg.product_id AS product_id,
                        SUM(a.amount) AS amount
                    FROM
                        contract_sections cs
                        JOIN
                        lot_groups lg
                        ON lg.contract_section_id = cs.id
                        JOIN
                        lots l
                        ON lg.id = l.lot_group_id
                        JOIN
                        allotment a
                        ON a.lot_id = l.id
                    WHERE
                        cs.archive_date IS NULL
                        AND a.launch_product_id IS NULL
                    GROUP BY
                        lg.product_id
                ) pretender
                ON pretender.product_id = p.id
                -- остаток заделов предыдущих запусков по договору
                LEFT JOIN (
                    SELECT
                        lp.product_id AS product_id,
                        SUM(lp.rf_contract_amount - lp.ufrf_contract_amount) AS amount
                    FROM
                        launch_product lp
                    WHERE
                        lp.launch_id < :launchId
                    GROUP BY
                        lp.product_id
                ) residueReserveContract
                ON residueReserveContract.product_id = p.id
            WHERE
                p.type_id IN (:productTypeListId)
                AND (:productName IS NULL OR :productName IS NOT NULL AND p.conditional_name LIKE :productName)
                AND (:hasPretender IS NULL OR :hasPretender IS NOT NULL AND (IFNULL(pretender.amount, 0) > 0) = :hasPretender)
                AND (:isLaunched IS NULL OR :isLaunched IS NOT NULL AND (IFNULL(lp.contract_amount, 0) > 0 OR IFNULL(lp.rf_contract_amount, 0) > 0 OR IFNULL(lp.rf_assembled_amount, 0) > 0) = :isLaunched)
            ${querySort.queryString(true)}
        """.trimIndent()
        val type = form.intNotNull(ObjAttr.TYPE)
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.LAUNCH_ID, launchId)
            .setParameter(ObjAttr.PRODUCT_TYPE_LIST_ID, listOf(1, 2, 3, 5, 6, 10))
            .setParameter(ObjAttr.PRODUCT_NAME, form.string(ObjAttr.PRODUCT_NAME).nullIfBlankOr { "%$it%" })
            .setParameter(ObjAttr.HAS_PRETENDER, if (type == PRETENDER_TYPE) true else if (type == LAUNCHED_TYPE) false else null)
            .setParameter(ObjAttr.IS_LAUNCHED, if (type == PRETENDER_TYPE || type == LAUNCHED_TYPE) true else null)
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.LP_ID, LongType.INSTANCE)
            .addScalar(ObjAttr.PRODUCT_TYPE_ID, LongType.INSTANCE)
            .addScalar(ObjAttr.HAS_PRETENDER, BooleanType.INSTANCE)
            .addScalar(ObjAttr.IS_LAUNCHED, BooleanType.INSTANCE)
            .addScalar(ObjAttr.PRODUCT_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.VER_ID, LongType.INSTANCE)
            .addScalar(ObjAttr.VER_MAJOR, IntegerType.INSTANCE)
            .addScalar(ObjAttr.VER_MINOR, IntegerType.INSTANCE)
            .addScalar(ObjAttr.VER_MOD, IntegerType.INSTANCE)
            .addScalar(ObjAttr.RESIDUE_RESERVE_CONTRACT, IntegerType.INSTANCE)
            .addScalar(ObjAttr.RESIDUE_RESERVE, IntegerType.INSTANCE)
            .addScalar(ObjAttr.IN_STRUCT_RESERVE, IntegerType.INSTANCE)
            .addScalar(ObjAttr.PRETENDERS, IntegerType.INSTANCE)
            .addScalar(ObjAttr.FOR_CONTRACT, IntegerType.INSTANCE)
            .addScalar(ObjAttr.RESERVE_CONTRACT, IntegerType.INSTANCE)
            .addScalar(ObjAttr.RESERVE, IntegerType.INSTANCE)
            .addScalar(ObjAttr.TOTAL, IntegerType.INSTANCE)
            .addScalar(ObjAttr.LAUNCH_IN_STRUCT_OTHER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.TOTAL_BEFORE_USED_RESERVE, IntegerType.INSTANCE)
            .addScalar(ObjAttr.USED_RESERVE_CONTRACT, IntegerType.INSTANCE)
            .addScalar(ObjAttr.IN_STRUCT_USED_RESERVE, IntegerType.INSTANCE)
            .addScalar(ObjAttr.USED_RESERVE_ASSEMBLE, IntegerType.INSTANCE)
            .addScalar(ObjAttr.TOTAL_USED_RESERVE, IntegerType.INSTANCE)
            .addScalar(ObjAttr.TOTAL_AFTER_USED_RESERVE, IntegerType.INSTANCE)
            .addScalar(ROW_COUNT_ALIAS, LongType.INSTANCE)
            .resultTransform(LaunchDetailListDto::class)
        nativeQuery.firstResult = input.start
        nativeQuery.maxResults = input.size
        return TabrResultQuery.instance(nativeQuery.typedManyResult())
    }
}