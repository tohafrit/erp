package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ProductSpecReviewPrice
import ru.korundm.repository.ProductSpecReviewPriceRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ProductSpecReviewPriceService : CommonService<ProductSpecReviewPrice> {

    fun deleteByJustificationId(id: Long)
    fun getAllByJustificationId(id: Long?): List<ProductSpecReviewPrice>
    fun deleteAll(objectList: List<ProductSpecReviewPrice>)
    fun deleteAllByIdList(idList: List<Long>)
    fun getByJustificationIdAndGroupId(justificationId: Long?, groupId: Long?): ProductSpecReviewPrice?
}

@Service
@Transactional
class ProductSpecReviewPriceServiceImpl(
    private val repository: ProductSpecReviewPriceRepository
) : ProductSpecReviewPriceService {

    private val cl = ProductSpecReviewPrice::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductSpecReviewPrice> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductSpecReviewPrice> = repository.findAllById(idList)

    override fun save(obj: ProductSpecReviewPrice): ProductSpecReviewPrice {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductSpecReviewPrice>): List<ProductSpecReviewPrice> = repository.saveAll(objectList)

    override fun read(id: Long): ProductSpecReviewPrice? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductSpecReviewPrice) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun deleteByJustificationId(id: Long) = repository.deleteByJustificationId(id)

    override fun getAllByJustificationId(id: Long?) = if (id == null) emptyList() else repository.findAllByJustificationId(id)

    override fun deleteAll(objectList: List<ProductSpecReviewPrice>) = repository.deleteAll()

    override fun deleteAllByIdList(idList: List<Long>) = repository.deleteAllByIdIn(idList)

    override fun getByJustificationIdAndGroupId(justificationId: Long?, groupId: Long?) = if (justificationId == null || groupId == null) null else repository.findFirstByJustificationIdAndGroupId(justificationId, groupId)
}