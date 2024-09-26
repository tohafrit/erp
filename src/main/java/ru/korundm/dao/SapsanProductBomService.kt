package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.SapsanProductBom
import ru.korundm.repository.SapsanProductBomRepository

interface SapsanProductBomService : CommonService<SapsanProductBom>

@Service
@Transactional
class SapsanProductBomServiceImpl(
    private val sapsanProductBomRepository: SapsanProductBomRepository
) : SapsanProductBomService {

    override fun getAll(): List<SapsanProductBom> = sapsanProductBomRepository.findAll()

    override fun getAllById(idList: List<Long>): List<SapsanProductBom> = sapsanProductBomRepository.findAllById(idList)

    override fun save(obj: SapsanProductBom) = sapsanProductBomRepository.save(obj)

    override fun saveAll(objectList: List<SapsanProductBom>): List<SapsanProductBom> = sapsanProductBomRepository.saveAll(objectList)

    override fun read(id: Long): SapsanProductBom? = sapsanProductBomRepository.findById(id).orElse(null)

    override fun delete(obj: SapsanProductBom) = sapsanProductBomRepository.delete(obj)

    override fun deleteById(id: Long) = sapsanProductBomRepository.deleteById(id)
}