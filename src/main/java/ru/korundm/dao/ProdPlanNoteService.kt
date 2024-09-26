package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ProdPlanNote
import ru.korundm.repository.ProdPlanNoteRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ProdPlanNoteService : CommonService<ProdPlanNote>

@Service
@Transactional
class ProdPlanNoteServiceImpl(
    private val repository: ProdPlanNoteRepository
): ProdPlanNoteService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProdPlanNote> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProdPlanNote> = repository.findAllById(idList)

    override fun save(obj: ProdPlanNote): ProdPlanNote {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProdPlanNote>): List<ProdPlanNote> = repository.saveAll(objectList)

    override fun read(id: Long): ProdPlanNote? = repository.findById(id).orElse(null)

    override fun delete(obj: ProdPlanNote) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)
}