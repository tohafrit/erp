package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ClassificationGroup
import ru.korundm.entity.Launch
import ru.korundm.repository.ClassificationGroupRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ClassificationGroupService : CommonService<ClassificationGroup> {

    fun existsByNumberAndIdNot(number: Int, id: Long?): Boolean
    fun getAllIdList(): List<Long>
    fun getAllByIdNotIn(idList: List<Long>): List<ClassificationGroup>
}

@Service
@Transactional
class ClassificationGroupServiceImpl(
    private val repository: ClassificationGroupRepository
) : ClassificationGroupService {

    private val cl = Launch::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ClassificationGroup> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ClassificationGroup> = repository.findAllById(idList)

    override fun save(obj: ClassificationGroup): ClassificationGroup {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ClassificationGroup>): List<ClassificationGroup> = repository.saveAll(objectList)

    override fun read(id: Long): ClassificationGroup? = repository.findById(id).orElse(null)

    override fun delete(obj: ClassificationGroup) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun existsByNumberAndIdNot(number: Int, id: Long?) = if (id == null) repository.existsByNumber(number) else repository.existsByNumberAndIdNot(number, id)

    override fun getAllIdList() = repository.findAllIdList()

    override fun getAllByIdNotIn(idList: List<Long>) = if (idList.isEmpty()) all else repository.findAllByIdNotIn(idList)
}