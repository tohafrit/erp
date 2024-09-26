package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.LaunchProductStruct
import ru.korundm.repository.LaunchProductStructRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface LaunchProductStructService : CommonService<LaunchProductStruct> {

    fun deleteAllByVersionIdAndProductId(versionId: Long?, productId: Long?)
    fun getAllByVersionIdAndProductId(versionId: Long?, productId: Long?): List<LaunchProductStruct>
    fun deleteAllByLaunchProductId(id: Long?)
}

@Service
@Transactional
class LaunchProductStructServiceImpl(
    private val repository: LaunchProductStructRepository
) : LaunchProductStructService {

    private val cl = LaunchProductStruct::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<LaunchProductStruct> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<LaunchProductStruct> = repository.findAllById(idList)

    override fun save(obj: LaunchProductStruct): LaunchProductStruct {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<LaunchProductStruct>): List<LaunchProductStruct> = repository.saveAll(objectList)

    override fun read(id: Long): LaunchProductStruct? = repository.findById(id).orElse(null)

    override fun delete(obj: LaunchProductStruct) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun deleteAllByVersionIdAndProductId(versionId: Long?, productId: Long?) = if (versionId == null || productId == null) Unit else repository.deleteAllByLaunchProductVersionIdAndLaunchProductVersionApproveDateIsNullAndProductId(versionId, productId)

    override fun getAllByVersionIdAndProductId(versionId: Long?, productId: Long?) = if (versionId == null || productId == null) emptyList() else repository.findAllByLaunchProductVersionIdAndLaunchProductVersionApproveDateIsNullAndProductId(versionId, productId)

    override fun deleteAllByLaunchProductId(id: Long?) = if (id == null) Unit else repository.deleteAllByLaunchProductId(id)
}