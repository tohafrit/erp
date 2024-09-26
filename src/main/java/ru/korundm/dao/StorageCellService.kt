package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.StorageCell
import ru.korundm.repository.StorageCellRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface StorageCellService : CommonService<StorageCell>

@Service
@Transactional
class StorageCellServiceImpl(
    private val repository: StorageCellRepository
) : StorageCellService {

    private val cl = StorageCell::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<StorageCell> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<StorageCell> = repository.findAllById(idList)

    override fun save(obj: StorageCell): StorageCell {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<StorageCell>): List<StorageCell> = repository.saveAll(objectList)

    override fun read(id: Long): StorageCell? = repository.findById(id).orElse(null)

    override fun delete(obj: StorageCell) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)
}