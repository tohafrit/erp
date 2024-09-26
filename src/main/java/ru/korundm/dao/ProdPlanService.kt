package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ProdPlan
import ru.korundm.repository.ProdPlanRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ProdPlanService : CommonService<ProdPlan>

@Service
@Transactional
class ProdPlanServiceImpl(
    private val repository: ProdPlanRepository
): ProdPlanService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProdPlan> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProdPlan> = repository.findAllById(idList)

    override fun save(obj: ProdPlan): ProdPlan {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProdPlan>): List<ProdPlan> = repository.saveAll(objectList)

    override fun read(id: Long): ProdPlan? = repository.findById(id).orElse(null)

    override fun delete(obj: ProdPlan) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)
}