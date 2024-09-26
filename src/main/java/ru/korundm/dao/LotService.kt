package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ContractSection
import ru.korundm.entity.Lot
import ru.korundm.repository.LotRepository

interface LotService : CommonService<Lot> {

    fun getLotListBySection(contractSection: ContractSection?): List<Lot>
    fun existsByPriceProtocolId(id: Long?): Boolean
}

@Service
@Transactional
class LotServiceImpl(
    private val repository: LotRepository
) : LotService {

    override fun getAll(): List<Lot> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<Lot> = repository.findAllById(idList)

    override fun save(obj: Lot) = repository.save(obj)

    override fun saveAll(objectList: List<Lot>): List<Lot> = repository.saveAll(objectList)

    override fun read(id: Long): Lot? = repository.findById(id).orElse(null)

    override fun delete(obj: Lot) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun getLotListBySection(contractSection: ContractSection?) = contractSection?.let { repository.findAllByLotGroupContractSection(it) } ?: emptyList()

    override fun existsByPriceProtocolId(id: Long?) = if (id == null) false else repository.existsByProtocolPriceProtocolId(id)
}