package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.LaborProtectionInstruction
import ru.korundm.repository.LaborProtectionInstructionRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Predicate

interface LaborProtectionInstructionService : CommonService<LaborProtectionInstruction> {

    fun findTableData(laborIdList: List<Long>): List<LaborProtectionInstruction>
}

@Service
@Transactional
class LaborProtectionInstructionServiceImpl(
    private val repository: LaborProtectionInstructionRepository
) : LaborProtectionInstructionService {

    private val cl = LaborProtectionInstruction::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<LaborProtectionInstruction> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<LaborProtectionInstruction> = repository.findAllById(idList)

    override fun save(obj: LaborProtectionInstruction): LaborProtectionInstruction {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<LaborProtectionInstruction>): List<LaborProtectionInstruction> = repository.saveAll(objectList)

    override fun read(id: Long): LaborProtectionInstruction? = repository.findById(id).orElse(null)

    override fun delete(obj: LaborProtectionInstruction) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(laborIdList: List<Long>): List<LaborProtectionInstruction> {
        val cb = em.criteriaBuilder
        val c = cb.createQuery(cl)
        val root = c.from(cl)
        val predicateList = mutableListOf<Predicate>()
        if (laborIdList.isNotEmpty()) predicateList += cb.not(root.get<Long>(ObjAttr.ID).`in`(laborIdList))
        val select = c.select(root).where(*predicateList.toTypedArray())
        return em.createQuery(select).resultList
    }
}
