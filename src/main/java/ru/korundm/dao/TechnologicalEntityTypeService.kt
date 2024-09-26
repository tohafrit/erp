package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.TechnologicalEntityType
import ru.korundm.repository.TechnologicalEntityTypeRepository

interface TechnologicalEntityTypeService : CommonService<TechnologicalEntityType> {

    fun findByName(name: String): TechnologicalEntityType?
}

@Service
@Transactional
class TechnologicalEntityTypeServiceImpl(
    private val repository: TechnologicalEntityTypeRepository
) : TechnologicalEntityTypeService {

    override fun getAll(): MutableList<TechnologicalEntityType> = repository.findAll()

    override fun getAllById(idList: MutableList<Long>): MutableList<TechnologicalEntityType> = repository.findAllById(idList)

    override fun save(`object`: TechnologicalEntityType): TechnologicalEntityType = repository.save(`object`)

    override fun saveAll(objectList: MutableList<TechnologicalEntityType>): MutableList<TechnologicalEntityType> = repository.saveAll(objectList)

    override fun read(id: Long): TechnologicalEntityType = repository.getOne(id)

    override fun delete(`object`: TechnologicalEntityType) = repository.delete(`object`)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findByName(name: String) = repository.findFirstByName(name)
}
