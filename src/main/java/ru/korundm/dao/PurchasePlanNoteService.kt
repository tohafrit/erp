package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.PurchasePlanNote
import ru.korundm.repository.PurchasePlanNoteRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface PurchasePlanNoteService : CommonService<PurchasePlanNote>

@Service
@Transactional
class PurchasePlanNoteServiceImpl(
    private val repository: PurchasePlanNoteRepository
) : PurchasePlanNoteService {

    private val cl = PurchasePlanNote::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<PurchasePlanNote> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<PurchasePlanNote> = repository.findAllById(idList)

    override fun save(obj: PurchasePlanNote): PurchasePlanNote {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<PurchasePlanNote>): List<PurchasePlanNote> = repository.saveAll(objectList)

    override fun read(id: Long): PurchasePlanNote? = repository.findById(id).orElse(null)

    override fun delete(obj: PurchasePlanNote) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)
}