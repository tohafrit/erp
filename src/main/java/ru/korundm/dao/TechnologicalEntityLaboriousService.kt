package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.*
import ru.korundm.repository.TechnologicalEntityLaboriousRepository

interface TechnologicalEntityLaboriousService : CommonService<TechnologicalEntityLaborious>

@Service
@Transactional
class TechnologicalEntityLaboriousServiceImpl(
    private val repository: TechnologicalEntityLaboriousRepository
) : TechnologicalEntityLaboriousService {

    override fun getAll(): List<TechnologicalEntityLaborious> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<TechnologicalEntityLaborious> = repository.findAllById(idList)

    override fun save(obj: TechnologicalEntityLaborious) = repository.save(obj)

    override fun saveAll(objectList: List<TechnologicalEntityLaborious>): List<TechnologicalEntityLaborious> = repository.saveAll(objectList)

    override fun read(id: Long): TechnologicalEntityLaborious? = repository.findById(id).orElse(null)

    override fun delete(obj: TechnologicalEntityLaborious) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)
}