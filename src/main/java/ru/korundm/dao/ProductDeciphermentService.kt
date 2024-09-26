package ru.korundm.dao

import eco.entity.EcoBomSpecItem
import eco.repository.EcoBomSpecItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant.UNDERSCORE
import ru.korundm.dto.decipherment.CompositionProduct
import ru.korundm.entity.ProductDecipherment
import ru.korundm.enumeration.ProductDeciphermentTypeEnum
import ru.korundm.repository.ProductDeciphermentRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ProductDeciphermentService : CommonService<ProductDecipherment> {

    fun getTypeIdListByPeriodId(periodId: Long): List<Long>
    fun existsByPeriodIdAndTypeIdIn(periodId: Long, typeIdList: List<Long>): Boolean
    fun existsById(id: Long?): Boolean
    fun getFirstByPeriodIdAndType(periodId: Long?, type: ProductDeciphermentTypeEnum): ProductDecipherment?
    fun verifyComposition(compositionProductList: List<CompositionProduct>): Boolean
    fun inapplicableCompositionProductList(compositionProductList: List<CompositionProduct>): List<CompositionProduct>
    fun getAllByPeriodId(periodId: Long?): List<ProductDecipherment>
    fun getAllApprovedByPeriodId(periodId: Long?): List<ProductDecipherment>
}

@Service
@Transactional
class ProductDeciphermentServiceImpl(
    private val ecoBomSpecItemRepository: EcoBomSpecItemRepository,
    private val repository: ProductDeciphermentRepository
) : ProductDeciphermentService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductDecipherment> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductDecipherment> = repository.findAllById(idList)

    override fun save(obj: ProductDecipherment): ProductDecipherment {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductDecipherment>): List<ProductDecipherment> = repository.saveAll(objectList)

    override fun read(id: Long): ProductDecipherment? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductDecipherment) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun existsById(id: Long?) = id != null && repository.existsById(id)

    override fun getFirstByPeriodIdAndType(periodId: Long?, type: ProductDeciphermentTypeEnum) = if (periodId == null) null else repository.findFirstByPeriodIdAndTypeId(periodId, type.id)

    override fun existsByPeriodIdAndTypeIdIn(periodId: Long, typeIdList: List<Long>) = repository.existsByPeriodIdAndTypeIdIn(periodId, typeIdList)

    override fun getTypeIdListByPeriodId(periodId: Long) = repository.findTypeIdListByPeriodId(periodId)

    override fun getAllByPeriodId(periodId: Long?) = if (periodId == null) emptyList() else repository.findAllByPeriodIdOrderByTypeSortAsc(periodId)

    override fun getAllApprovedByPeriodId(periodId: Long?) = if (periodId == null) emptyList() else repository.findAllByApprovedAndPeriodIdOrderByTypeSortAsc(true, periodId)

    /**
     * Получение списка изделий, которые не были найдены в структуре иерархии состава
     * @param compositionProductList список сохраненных изделий состава
     * @return список изделий, которые не были найдены в структуре иерархии состава
     */
    override fun inapplicableCompositionProductList(compositionProductList: List<CompositionProduct>): List<CompositionProduct> {
        val inapplicableProductList = mutableListOf<CompositionProduct>()
        // Делаем проход по древу структуры от сохраненного изделия до своих потомков
        for (compositionProduct in compositionProductList) {
            val numberArr = compositionProduct.fullHierarchyNumber.split(UNDERSCORE).toTypedArray()
            val ecoBomSpecItem = ecoBomSpecItemRepository.getFirstByIdAndProductIdAndBomId(
                compositionProduct.specificationId,
                compositionProduct.productId,
                compositionProduct.versionId
            )
            val specItemList = mutableListOf<EcoBomSpecItem>()
            if (ecoBomSpecItem != null) specItemList.add(ecoBomSpecItem)
            val tempSpecItemList = mutableListOf<EcoBomSpecItem>()
            var isExists = false
            for (i in numberArr.indices.reversed()) {
                val idArr = numberArr[i].split("-").toTypedArray()
                val specificationId = idArr[0].toLong()
                val productId = idArr[1].toLong()
                val versionId = idArr[2].toLong()
                for (specItem in specItemList) {
                    if (specItem.id == specificationId && specItem.product.id == productId && specItem.bom.id == versionId) {
                        if (i == 0) { // Дошли до конца и получили совпадение - изделие состава есть в иерархии
                            isExists = true
                        } else {
                            tempSpecItemList.addAll(specItem.bom.product.ecoBomSpecItemList)
                        }
                    }
                }
                specItemList.clear()
                specItemList.addAll(tempSpecItemList)
                tempSpecItemList.clear()
            }
            if (!isExists) inapplicableProductList.add(compositionProduct)
        }
        return inapplicableProductList
    }

    /**
     * Метод верификации состава изделия. Выполняет проверку состава изделия на соответствие иерархии
     * @param compositionProductList список изделий состава
     * @return true - соответствует составу, иначе - false
     */
    override fun verifyComposition(compositionProductList: List<CompositionProduct>) = inapplicableCompositionProductList(compositionProductList).isEmpty()
}