package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.*
import ru.korundm.repository.TechnologicalEntityApplicabilityRepository

interface TechnologicalEntityApplicabilityService : CommonService<TechnologicalEntityApplicability> {

    fun deleteAllByTechnologicalEntity(technologicalEntity: TechnologicalEntity)
}

@Service
@Transactional
class TechnologicalEntityApplicabilityServiceImpl(
    private val repository: TechnologicalEntityApplicabilityRepository
) : TechnologicalEntityApplicabilityService {

    private val cl = TechnologicalEntityApplicability::class.java

    override fun getAll(): List<TechnologicalEntityApplicability> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<TechnologicalEntityApplicability> = repository.findAllById(idList)

    override fun save(obj: TechnologicalEntityApplicability) = repository.save(obj)

    override fun saveAll(objectList: List<TechnologicalEntityApplicability>): List<TechnologicalEntityApplicability> = repository.saveAll(objectList)

    override fun read(id: Long): TechnologicalEntityApplicability? = repository.findById(id).orElse(null)

    override fun delete(obj: TechnologicalEntityApplicability) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun deleteAllByTechnologicalEntity(technologicalEntity: TechnologicalEntity) = repository.deleteAllByTechnologicalEntity(technologicalEntity)
}
