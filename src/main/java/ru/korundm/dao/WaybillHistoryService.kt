package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.WaybillHistory
import ru.korundm.repository.WaybillHistoryRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface WaybillHistoryService : CommonService<WaybillHistory> {

    fun deleteAllByShipmentId(id: Long?)
    fun deleteAllByInternalId(id: Long?)
    fun getAllByMatValueId(id: Long?): List<WaybillHistory>
}

@Service
@Transactional
class WaybillHistoryServiceImpl(
    private val repository: WaybillHistoryRepository
) : WaybillHistoryService {

    private val cl = WaybillHistory::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<WaybillHistory> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<WaybillHistory> = repository.findAllById(idList)

    override fun save(obj: WaybillHistory): WaybillHistory {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<WaybillHistory>): List<WaybillHistory> = repository.saveAll(objectList)

    override fun read(id: Long): WaybillHistory? = repository.findById(id).orElse(null)

    override fun delete(obj: WaybillHistory) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun deleteAllByShipmentId(id: Long?) = if (id == null) Unit else repository.deleteAllByShipmentId(id)

    override fun deleteAllByInternalId(id: Long?) = if (id == null) Unit else repository.deleteAllByInternalId(id)

    override fun getAllByMatValueId(id: Long?) = if (id == null) emptyList() else repository.findAllByMatValueIdOrderByCreateDateAsc(id)
}