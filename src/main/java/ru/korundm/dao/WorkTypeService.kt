package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.WorkType
import ru.korundm.repository.WorkTypeRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface WorkTypeService : CommonService<WorkType> {

    fun existsByNameAndIdNot(name: String, id: Long?): Boolean
    fun getAllByIdNotIn(idList: List<Long>): List<WorkType>
    fun getAllIdList(): List<Long>
    fun getByName(name: String): WorkType?
}

@Service
@Transactional
class WorkTypeServiceImpl(
    private val repository: WorkTypeRepository
) : WorkTypeService {

    private val cl = WorkType::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<WorkType> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<WorkType> = repository.findAllById(idList)

    override fun save(obj: WorkType): WorkType {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<WorkType>): List<WorkType> = repository.saveAll(objectList)

    override fun read(id: Long): WorkType? = repository.findById(id).orElse(null)

    override fun delete(obj: WorkType) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun existsByNameAndIdNot(name: String, id: Long?) = if (id == null) repository.existsByName(name) else repository.existsByNameAndIdNot(name, id)

    override fun getAllByIdNotIn(idList: List<Long>) = if (idList.isEmpty()) all else repository.findAllByIdNotIn(idList)

    override fun getAllIdList() = repository.findAllIdList()

    override fun getByName(name: String) = repository.findFirstByName(name)
}