package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ProductSpecResearchPrice
import ru.korundm.repository.ProductSpecResearchPriceRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ProductSpecResearchPriceService : CommonService<ProductSpecResearchPrice> {

    fun deleteByJustificationId(id: Long)
    fun getAllByJustificationId(id: Long?): List<ProductSpecResearchPrice>
    fun deleteAll(objectList: List<ProductSpecResearchPrice>)
    fun getByJustificationIdAndGroupId(justificationId: Long?, groupId: Long?): ProductSpecResearchPrice?
    fun deleteAllByIdList(idList: List<Long>)
}

@Service
@Transactional
class ProductSpecResearchPriceServiceImpl(
    private val repository: ProductSpecResearchPriceRepository
) : ProductSpecResearchPriceService {

    private val cl = ProductSpecResearchPrice::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductSpecResearchPrice> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductSpecResearchPrice> = repository.findAllById(idList)

    override fun save(obj: ProductSpecResearchPrice): ProductSpecResearchPrice {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductSpecResearchPrice>): List<ProductSpecResearchPrice> = repository.saveAll(objectList)

    override fun read(id: Long): ProductSpecResearchPrice? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductSpecResearchPrice) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun deleteByJustificationId(id: Long) = repository.deleteByJustificationId(id)

    override fun getAllByJustificationId(id: Long?) = if (id == null) emptyList() else repository.findAllByJustificationId(id)

    override fun deleteAll(objectList: List<ProductSpecResearchPrice>) = repository.deleteAll()

    override fun getByJustificationIdAndGroupId(justificationId: Long?, groupId: Long?) = if (justificationId == null || groupId == null) null else repository.findFirstByJustificationIdAndGroupId(justificationId, groupId)

    override fun deleteAllByIdList(idList: List<Long>) = repository.deleteAllByIdIn(idList)
}