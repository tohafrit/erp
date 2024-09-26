package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ProductWorkCost
import ru.korundm.repository.ProductWorkCostRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ProductWorkCostService : CommonService<ProductWorkCost> {

    fun getAllByJustificationId(id: Long?): List<ProductWorkCost>
    fun deleteByJustificationId(id: Long)
    fun deleteAll(objectList: List<ProductWorkCost>)
}

@Service
@Transactional
class ProductWorkCostServiceImpl(
    private val repository: ProductWorkCostRepository
) : ProductWorkCostService {

    private val cl = ProductWorkCost::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductWorkCost> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductWorkCost> = repository.findAllById(idList)

    override fun save(obj: ProductWorkCost): ProductWorkCost {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductWorkCost>): List<ProductWorkCost> = repository.saveAll(objectList)

    override fun read(id: Long): ProductWorkCost? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductWorkCost) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun getAllByJustificationId(id: Long?) = if (id == null) emptyList() else repository.findAllByJustificationId(id)

    override fun deleteByJustificationId(id: Long) = repository.deleteByJustificationId(id)

    override fun deleteAll(objectList: List<ProductWorkCost>) = repository.deleteAll(objectList)
}