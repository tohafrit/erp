package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.TechnologicalEntity
import ru.korundm.entity.TechnologicalEntityReconciliation
import ru.korundm.repository.TechnologicalEntityReconciliationRepository

interface TechnologicalEntityReconciliationService : CommonService<TechnologicalEntityReconciliation> {

    fun getTopByTechnologicalEntity(technologicalEntity: TechnologicalEntity): TechnologicalEntityReconciliation?
}

@Service
@Transactional
class TechnologicalEntityReconciliationServiceImpl(
    private val repository: TechnologicalEntityReconciliationRepository
) : TechnologicalEntityReconciliationService {

    override fun getAll(): List<TechnologicalEntityReconciliation> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<TechnologicalEntityReconciliation> = repository.findAllById(idList)

    override fun save(obj: TechnologicalEntityReconciliation) = repository.save(obj)

    override fun saveAll(objectList: List<TechnologicalEntityReconciliation>): List<TechnologicalEntityReconciliation> = repository.saveAll(objectList)

    override fun read(id: Long): TechnologicalEntityReconciliation? = repository.findById(id).orElse(null)

    override fun delete(obj: TechnologicalEntityReconciliation) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun getTopByTechnologicalEntity(technologicalEntity: TechnologicalEntity): TechnologicalEntityReconciliation? = repository.findTopByTechnologicalEntityOrderByIdDesc(technologicalEntity)
}
