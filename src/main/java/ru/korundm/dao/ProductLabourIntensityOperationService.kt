package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ProductLabourIntensityOperation
import ru.korundm.repository.ProductLabourIntensityOperationRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ProductLabourIntensityOperationService : CommonService<ProductLabourIntensityOperation> {

    fun getAllByEntryId(entryId: Long): List<ProductLabourIntensityOperation>
    fun deleteAllByEntryId(entryId: Long?)
    fun getAllByLabourIntensityIdAndProductId(labourIntensityId: Long?, productId: Long?): List<ProductLabourIntensityOperation>
    fun deleteAll(list: List<ProductLabourIntensityOperation>)
    fun deleteAllByJustificationId(id: Long?)
    fun deleteAllByIdList(idList: List<Long>)
}

@Service
@Transactional
class ProductLabourIntensityOperationServiceImpl(
    private val repository: ProductLabourIntensityOperationRepository
) : ProductLabourIntensityOperationService {

    private val cl = ProductLabourIntensityOperation::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductLabourIntensityOperation> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductLabourIntensityOperation> = repository.findAllById(idList)

    override fun save(obj: ProductLabourIntensityOperation): ProductLabourIntensityOperation {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductLabourIntensityOperation>): List<ProductLabourIntensityOperation> = repository.saveAll(objectList)

    override fun read(id: Long): ProductLabourIntensityOperation? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductLabourIntensityOperation) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun getAllByEntryId(entryId: Long) = repository.findAllByEntryId(entryId)

    override fun deleteAllByEntryId(entryId: Long?) = if (entryId == null) Unit else repository.deleteAllByEntryId(entryId)

    override fun getAllByLabourIntensityIdAndProductId(labourIntensityId: Long?, productId: Long?) =
        if (labourIntensityId == null || productId == null) emptyList() else repository.findAllByEntryLabourIntensityIdAndEntryProductId(labourIntensityId, productId)

    override fun deleteAll(list: List<ProductLabourIntensityOperation>) = repository.deleteAll(list)

    override fun deleteAllByJustificationId(id: Long?) = if (id == null) Unit else repository.deleteAllByEntryLabourIntensityId(id)

    override fun deleteAllByIdList(idList: List<Long>) = repository.deleteAllByIdIn(idList)
}