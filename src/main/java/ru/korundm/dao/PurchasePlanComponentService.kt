package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.PurchasePlanComponent
import ru.korundm.repository.PurchasePlanComponentRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface PurchasePlanComponentService : CommonService<PurchasePlanComponent>

@Service
@Transactional
class PurchasePlanComponentServiceImpl(
    private val repository: PurchasePlanComponentRepository
) : PurchasePlanComponentService {

    private val cl = PurchasePlanComponent::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<PurchasePlanComponent> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<PurchasePlanComponent> = repository.findAllById(idList)

    override fun save(obj: PurchasePlanComponent): PurchasePlanComponent {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<PurchasePlanComponent>): List<PurchasePlanComponent> = repository.saveAll(objectList)

    override fun read(id: Long): PurchasePlanComponent? = repository.findById(id).orElse(null)

    override fun delete(obj: PurchasePlanComponent) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)
}