package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.Subdivision
import ru.korundm.repository.SubdivisionRepository

interface SubdivisionService : CommonService<Subdivision> {

    fun getByOnecId(onecId: String): Subdivision?
    fun getAllActive(onecId: String): List<Subdivision>
}

@Service
@Transactional
class SubdivisionServiceImpl(
    private val subdivisionRepository: SubdivisionRepository
) : SubdivisionService {
    override fun getAll(): List<Subdivision> = subdivisionRepository.findAll()

    override fun getAllById(idList: List<Long>): List<Subdivision> = subdivisionRepository.findAllById(idList)

    override fun save(obj: Subdivision) = subdivisionRepository.save(obj)

    override fun saveAll(objectList: List<Subdivision>): List<Subdivision> = subdivisionRepository.saveAll(objectList)

    override fun read(id: Long): Subdivision? = subdivisionRepository.findById(id).orElse(null)

    override fun delete(obj: Subdivision) = subdivisionRepository.delete(obj)

    override fun deleteById(id: Long) = subdivisionRepository.deleteById(id)

    override fun getByOnecId(onecId: String): Subdivision? = subdivisionRepository.findFirstByOnecId(onecId)

    override fun getAllActive(onecId: String): List<Subdivision> = subdivisionRepository.findAllByFormedIsTrueAndDisbandedIsFalseAndParentIdOrderBySort(onecId)
}