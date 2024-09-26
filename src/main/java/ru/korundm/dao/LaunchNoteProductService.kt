package ru.korundm.dao

import org.hibernate.query.NativeQuery
import org.hibernate.type.IntegerType
import org.hibernate.type.LongType
import org.hibernate.type.StringType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.constant.BaseConstant.ROW_COUNT_ALIAS
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.LaunchNote
import ru.korundm.entity.LaunchNoteProduct
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.RowCountable
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.LaunchNoteProductRepository
import ru.korundm.util.KtCommonUtil.ifNotBlank
import ru.korundm.util.KtCommonUtil.resultTransform
import ru.korundm.util.KtCommonUtil.typedManyResult
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import kotlin.reflect.KClass

interface LaunchNoteProductService : CommonService<LaunchNoteProduct> {

    fun existsByNote(note: LaunchNote): Boolean
    fun <T : RowCountable> findNoteProductTableData(
        tableInput: TabrIn,
        noteId: Long,
        form: DynamicObject,
        transformClass: KClass<T>
    ): TabrResultQuery<T>
}

@Service
@Transactional
class LaunchNoteProductServiceImpl(
    private val repository: LaunchNoteProductRepository
): LaunchNoteProductService {

    private val cl = LaunchNoteProduct::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<LaunchNoteProduct> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<LaunchNoteProduct> = repository.findAllById(idList)

    override fun save(obj: LaunchNoteProduct): LaunchNoteProduct {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<LaunchNoteProduct>): List<LaunchNoteProduct> = repository.saveAll(objectList)

    override fun read(id: Long): LaunchNoteProduct? = repository.findById(id).orElse(null)

    override fun delete(obj: LaunchNoteProduct) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun existsByNote(note: LaunchNote) = repository.existsByNote(note)

    override fun <T : RowCountable> findNoteProductTableData(
        tableInput: TabrIn,
        noteId: Long,
        form: DynamicObject,
        transformClass: KClass<T>
    ): TabrResultQuery<T> {
        val query = """
            SELECT
                lnp.id AS ${ObjAttr.ID},
                l.year AS ${ObjAttr.YEAR},
                l.number AS ${ObjAttr.NUMBER},
                pl.number AS ${ObjAttr.PARENT_NUMBER},
                p.conditional_name AS ${ObjAttr.PRODUCT_NAME},
                lnp.contract_amount AS ${ObjAttr.CONTRACT_AMOUNT},
                lnp.rf_contract_amount AS ${ObjAttr.RF_CONTRACT_AMOUNT},
                lnp.rf_assembled_amount AS ${ObjAttr.RF_ASSEMBLED_AMOUNT},
                lnp.ufrf_contract_amount AS ${ObjAttr.UFRF_CONTRACT_AMOUNT},
                lnp.ufrf_assembled_amount AS ${ObjAttr.UFRF_ASSEMBLED_AMOUNT},
                lnp.ufrf_contract_in_other_product_amount AS ${ObjAttr.UFRF_CONTRACT_IN_OTHER_PRODUCT_AMOUNT},
                $PART_QUERY_COUNT_OVER $ROW_COUNT_ALIAS
            FROM
                launch_note_product lnp
                JOIN
                launch_product lp ON lnp.launch_product_id = lp.id
                JOIN
                launch l ON lp.launch_id = l.id
                JOIN
                products p ON lp.product_id = p.id
                LEFT JOIN
                launch pl ON pl.id = l.launch_id
            WHERE
                lnp.note_id = :noteId
                AND (:productName = '' OR :productName <> '' AND p.conditional_name LIKE :productName)
                AND (:launchId = 0 OR :launchId <> 0 AND l.id = :launchId)
            ORDER BY
                pl.year IS NOT NULL,
                pl.year DESC,
                ${ObjAttr.PARENT_NUMBER} DESC,
                ${ObjAttr.YEAR} DESC,
                ${ObjAttr.NUMBER} DESC
        """.trimIndent()
        val launchId = form.longNotNull(ObjAttr.LAUNCH_ID)
        val productName = form.stringNotNull(ObjAttr.PRODUCT_NAME)
        val nativeQuery = em.createNativeQuery(query)
            .setParameter(ObjAttr.NOTE_ID, noteId)
            .setParameter(ObjAttr.LAUNCH_ID, launchId)
            .setParameter(ObjAttr.PRODUCT_NAME, productName.ifNotBlank { "%${it}%" })
        nativeQuery.firstResult = tableInput.start
        nativeQuery.maxResults = tableInput.size
        nativeQuery.unwrap(NativeQuery::class.java)
            .addScalar(ObjAttr.ID, LongType.INSTANCE)
            .addScalar(ObjAttr.YEAR, IntegerType.INSTANCE)
            .addScalar(ObjAttr.NUMBER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.PARENT_NUMBER, IntegerType.INSTANCE)
            .addScalar(ObjAttr.PRODUCT_NAME, StringType.INSTANCE)
            .addScalar(ObjAttr.CONTRACT_AMOUNT, IntegerType.INSTANCE)
            .addScalar(ObjAttr.RF_CONTRACT_AMOUNT, IntegerType.INSTANCE)
            .addScalar(ObjAttr.RF_ASSEMBLED_AMOUNT, IntegerType.INSTANCE)
            .addScalar(ObjAttr.UFRF_CONTRACT_AMOUNT, IntegerType.INSTANCE)
            .addScalar(ObjAttr.UFRF_ASSEMBLED_AMOUNT, IntegerType.INSTANCE)
            .addScalar(ObjAttr.UFRF_CONTRACT_IN_OTHER_PRODUCT_AMOUNT, IntegerType.INSTANCE)
            .addScalar(ROW_COUNT_ALIAS, LongType.INSTANCE)
            .resultTransform(transformClass)
        return TabrResultQuery.instance(nativeQuery.typedManyResult())
    }
}