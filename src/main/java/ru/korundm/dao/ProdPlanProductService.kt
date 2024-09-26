package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ProdPlanProduct
import ru.korundm.repository.ProdPlanProductRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ProdPlanProductService : CommonService<ProdPlanProduct>

@Service
@Transactional
class ProdPlanProductServiceImpl(
    private val repository: ProdPlanProductRepository
): ProdPlanProductService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProdPlanProduct> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProdPlanProduct> = repository.findAllById(idList)

    override fun save(obj: ProdPlanProduct): ProdPlanProduct {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProdPlanProduct>): List<ProdPlanProduct> = repository.saveAll(objectList)

    override fun read(id: Long): ProdPlanProduct? = repository.findById(id).orElse(null)

    override fun delete(obj: ProdPlanProduct) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)
}