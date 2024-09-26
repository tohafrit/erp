package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.StoragePlace
import ru.korundm.repository.StoragePlaceRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface StoragePlaceService : CommonService<StoragePlace>

@Service
@Transactional
class StoragePlaceServiceImpl(
    private val repository: StoragePlaceRepository
) : StoragePlaceService {

    private val cl = StoragePlace::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<StoragePlace> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<StoragePlace> = repository.findAllById(idList)

    override fun save(obj: StoragePlace): StoragePlace {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<StoragePlace>): List<StoragePlace> = repository.saveAll(objectList)

    override fun read(id: Long): StoragePlace? = repository.findById(id).orElse(null)

    override fun delete(obj: StoragePlace) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)
}