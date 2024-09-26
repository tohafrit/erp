package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.TechnologicalEntityAgreed
import ru.korundm.repository.TechnologicalEntityAgreedRepository

interface TechnologicalEntityAgreedService : CommonService<TechnologicalEntityAgreed> {

    fun deleteAll(list: List<TechnologicalEntityAgreed>)
}

@Service
@Transactional
class TechnologicalEntityAgreedServiceImpl(
    private val repository: TechnologicalEntityAgreedRepository
) : TechnologicalEntityAgreedService {

    override fun getAll(): List<TechnologicalEntityAgreed> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<TechnologicalEntityAgreed> = repository.findAllById(idList)

    override fun save(obj: TechnologicalEntityAgreed) = repository.save(obj)

    override fun saveAll(objectList: List<TechnologicalEntityAgreed>): List<TechnologicalEntityAgreed> = repository.saveAll(objectList)

    override fun read(id: Long): TechnologicalEntityAgreed? = repository.findById(id).orElse(null)

    override fun delete(obj: TechnologicalEntityAgreed) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun deleteAll(list: List<TechnologicalEntityAgreed>) = repository.deleteAll(list)
}
