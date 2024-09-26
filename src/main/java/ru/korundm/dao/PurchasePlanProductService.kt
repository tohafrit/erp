package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.PurchasePlanProduct
import ru.korundm.repository.PurchasePlanProductRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface PurchasePlanProductService  : CommonService<PurchasePlanProduct>

@Service
@Transactional
class PurchasePlanProductServiceImpl(
    private val repository: PurchasePlanProductRepository
) : PurchasePlanProductService {

    private val cl = PurchasePlanProduct::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<PurchasePlanProduct> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<PurchasePlanProduct> = repository.findAllById(idList)

    override fun save(obj: PurchasePlanProduct): PurchasePlanProduct {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<PurchasePlanProduct>): List<PurchasePlanProduct> = repository.saveAll(objectList)

    override fun read(id: Long): PurchasePlanProduct? = repository.findById(id).orElse(null)

    override fun delete(obj: PurchasePlanProduct) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)
}