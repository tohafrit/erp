package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.LotGroup
import ru.korundm.entity.ServiceType
import ru.korundm.repository.LotGroupRepository

interface LotGroupService : CommonService<LotGroup> {

    fun existsByServiceType(type: ServiceType): Boolean
}

@Service
@Transactional
class LotGroupServiceImpl(
    private val lotGroupRepository: LotGroupRepository
) : LotGroupService {

    override fun getAll(): List<LotGroup> = lotGroupRepository.findAll()

    override fun getAllById(idList: List<Long>): List<LotGroup> = lotGroupRepository.findAllById(idList)

    override fun save(obj: LotGroup) = lotGroupRepository.save(obj)

    override fun saveAll(objectList: List<LotGroup>): List<LotGroup> = lotGroupRepository.saveAll(objectList)

    override fun read(id: Long): LotGroup? = lotGroupRepository.findById(id).orElse(null)

    override fun delete(obj: LotGroup) = lotGroupRepository.delete(obj)

    override fun deleteById(id: Long) = lotGroupRepository.deleteById(id)

    override fun existsByServiceType(type: ServiceType) = lotGroupRepository.existsByServiceType(type)
}