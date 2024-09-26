package ru.korundm.dao

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ProductDecipherment
import ru.korundm.entity.ProductDeciphermentAttrVal
import ru.korundm.enumeration.ProductDeciphermentAttr
import ru.korundm.enumeration.ProductDeciphermentTypeEnum
import ru.korundm.repository.ProductDeciphermentAttrValRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ProductDeciphermentAttrValService : CommonService<ProductDeciphermentAttrVal> {

    fun getAttributeValueList(
        attrList: List<ProductDeciphermentAttr>,
        typeList: List<ProductDeciphermentTypeEnum>,
        periodId: Long?
    ): List<ProductDeciphermentAttrVal>

    fun getAttributeValueList(
        attr: ProductDeciphermentAttr,
        typeList: List<ProductDeciphermentTypeEnum>,
        periodId: Long?
    ): List<ProductDeciphermentAttrVal>

    fun getAttributeValueList(
        attr: ProductDeciphermentAttr,
        type: ProductDeciphermentTypeEnum,
        periodId: Long?
    ): List<ProductDeciphermentAttrVal>

    fun getAttributeValue(
        attr: ProductDeciphermentAttr,
        type: ProductDeciphermentTypeEnum,
        periodId: Long?
    ): ProductDeciphermentAttrVal?

    fun getFirstByDeciphermentIdAndAttribute(
        deciphermentId: Long?,
        attr: ProductDeciphermentAttr
    ): ProductDeciphermentAttrVal?

    fun getFirstByDeciphermentAndAttributeKey(
        decipherment: ProductDecipherment?,
        attr: ProductDeciphermentAttr
    ): ProductDeciphermentAttrVal?

    fun readLongValue(deciphermentId: Long?, attr: ProductDeciphermentAttr): Long?
    fun readLongValue(decipherment: ProductDecipherment?, attr: ProductDeciphermentAttr): Long?
    fun readJSONValue(deciphermentId: Long?, attr: ProductDeciphermentAttr): String?
    fun readJSONValue(decipherment: ProductDecipherment?, attr: ProductDeciphermentAttr): String?
    fun <T> readDataJSON(deciphermentId: Long?, attr: ProductDeciphermentAttr, typeRef: TypeReference<T>): T?
    fun <T> readDataJSON(decipherment: ProductDecipherment?, attr: ProductDeciphermentAttr, typeRef: TypeReference<T>): T?
    fun existsByProductWorkCostJustificationId(id: Long?): Boolean
    fun existsByProductSpecResearchJustificationId(id: Long?): Boolean
    fun existsByProductSpecReviewJustificationId(id: Long?): Boolean
    fun existsByProductLabourIntensityId(id: Long?): Boolean
    fun existsByProductIdAndLabourIntensityId(productId: Long?, labourIntensityId: Long?): Boolean
    fun deleteAllByDeciphermentId(deciphermentId: Long)
}

@Service
@Transactional
class ProductDeciphermentAttrValServiceImpl(
    private val jsonMapper: ObjectMapper,
    private val repository: ProductDeciphermentAttrValRepository
) : ProductDeciphermentAttrValService {

    private enum class ValueType { STRING, LONG, BOOL, DATE, DATETIME, JSON }

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductDeciphermentAttrVal> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductDeciphermentAttrVal> = repository.findAllById(idList)

    override fun save(obj: ProductDeciphermentAttrVal): ProductDeciphermentAttrVal {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductDeciphermentAttrVal>): List<ProductDeciphermentAttrVal> = repository.saveAll(objectList)

    override fun read(id: Long): ProductDeciphermentAttrVal? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductDeciphermentAttrVal) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun getAttributeValueList(
        attrList: List<ProductDeciphermentAttr>,
        typeList: List<ProductDeciphermentTypeEnum>,
        periodId: Long?
    ) = if (attrList.isEmpty() || typeList.isEmpty() || periodId == null) emptyList() else repository.findAllByAttributeInAndDeciphermentTypeIdInAndDeciphermentPeriodId(
        attrList,
        typeList.map { it.id },
        periodId
    )

    override fun getAttributeValueList(
        attr: ProductDeciphermentAttr,
        typeList: List<ProductDeciphermentTypeEnum>,
        periodId: Long?
    ) = if (typeList.isEmpty() || periodId == null) emptyList() else repository.findAllByAttributeAndDeciphermentTypeIdInAndDeciphermentPeriodId(
        attr,
        typeList.map { it.id },
        periodId
    )

    override fun getAttributeValueList(
        attr: ProductDeciphermentAttr,
        type: ProductDeciphermentTypeEnum,
        periodId: Long?
    ) = if (periodId == null) emptyList() else repository.findAllByAttributeAndDeciphermentTypeIdAndDeciphermentPeriodId(
        attr,
        type.id,
        periodId
    )

    override fun getAttributeValue(
        attr: ProductDeciphermentAttr,
        type: ProductDeciphermentTypeEnum,
        periodId: Long?
    ) = getAttributeValueList(attr, type, periodId).firstOrNull()

    override fun getFirstByDeciphermentIdAndAttribute(
        deciphermentId: Long?,
        attr: ProductDeciphermentAttr
    ) = if (deciphermentId == null) null else repository.findFirstByDeciphermentIdAndAttribute(deciphermentId, attr)

    override fun getFirstByDeciphermentAndAttributeKey(
        decipherment: ProductDecipherment?,
        attr: ProductDeciphermentAttr
    ) = getFirstByDeciphermentIdAndAttribute(decipherment?.id, attr)

    private fun readValue(deciphermentId: Long?, attr: ProductDeciphermentAttr, valType: ValueType) =
        getFirstByDeciphermentIdAndAttribute(deciphermentId, attr)?.let { when (valType) {
            ValueType.STRING -> it.stringVal
            ValueType.LONG -> it.longVal
            ValueType.BOOL -> it.boolVal
            ValueType.DATE -> it.dateVal
            ValueType.DATETIME -> it.datetimeVal
            ValueType.JSON -> it.jsonVal
        } }

    override fun readLongValue(deciphermentId: Long?, attr: ProductDeciphermentAttr) = readValue(deciphermentId, attr, ValueType.LONG) as? Long

    override fun readLongValue(decipherment: ProductDecipherment?, attr: ProductDeciphermentAttr) = readLongValue(decipherment?.id, attr)

    override fun readJSONValue(deciphermentId: Long?, attr: ProductDeciphermentAttr) = readValue(deciphermentId, attr, ValueType.JSON) as? String

    override fun readJSONValue(decipherment: ProductDecipherment?, attr: ProductDeciphermentAttr) = readJSONValue(decipherment?.id, attr)

    override fun <T> readDataJSON(deciphermentId: Long?, attr: ProductDeciphermentAttr, typeRef: TypeReference<T>)= readJSONValue(deciphermentId, attr)?.let { jsonMapper.readValue(it, typeRef) }

    override fun <T> readDataJSON(decipherment: ProductDecipherment?, attr: ProductDeciphermentAttr, typeRef: TypeReference<T>)= readDataJSON(decipherment?.id, attr, typeRef)

    override fun existsByProductWorkCostJustificationId(id: Long?) = if (id == null) false else repository.existsByProductWorkCostJustificationId(id)

    override fun existsByProductSpecResearchJustificationId(id: Long?) = if (id == null) false else repository.existsByProductSpecResearchJustificationId(id)

    override fun existsByProductSpecReviewJustificationId(id: Long?) = if (id == null) false else repository.existsByProductSpecReviewJustificationId(id)

    override fun existsByProductLabourIntensityId(id: Long?) = if (id == null) false else repository.existsByProductLabourIntensityId(id)

    override fun existsByProductIdAndLabourIntensityId(productId: Long?, labourIntensityId: Long?) =
        if (productId == null || labourIntensityId == null) false else repository.existsByDeciphermentPeriodProductIdAndProductLabourIntensityId(productId, labourIntensityId)

    override fun deleteAllByDeciphermentId(deciphermentId: Long) = repository.deleteAllByDeciphermentId(deciphermentId)
}