package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ValueAddedTax
import ru.korundm.repository.ValueAddedTaxRepository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ValueAddedTaxService : CommonService<ValueAddedTax> {

    fun findLastTwoElementsByDate(): List<ValueAddedTax>
    fun findByDateFrom(date: LocalDate?): ValueAddedTax?
    fun getDateFromLast(): ValueAddedTax?
    fun getAllByShipmentWaybillId(id: Long?): List<ValueAddedTax>
}

@Service
@Transactional
class ValueAddedTaxServiceImpl(
    private val repository: ValueAddedTaxRepository
) : ValueAddedTaxService {

    private val cl = ValueAddedTax::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ValueAddedTax> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ValueAddedTax> = repository.findAllById(idList)

    override fun save(obj: ValueAddedTax): ValueAddedTax {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ValueAddedTax>): List<ValueAddedTax> = repository.saveAll(objectList)

    override fun read(id: Long): ValueAddedTax? = repository.findById(id).orElse(null)

    override fun delete(obj: ValueAddedTax) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findLastTwoElementsByDate(): List<ValueAddedTax> {
        val valueAddedTaxList = repository.findTop2ByOrderByDateFromDesc()
        val result: MutableList<ValueAddedTax> = ArrayList()
        if (valueAddedTaxList.isNotEmpty()) result.add(valueAddedTaxList[0])
        if (valueAddedTaxList.size > 1) result.add(valueAddedTaxList[1])
        return result
    }

    override fun findByDateFrom(date: LocalDate?) = date?.let { repository.findTopByDateFromLessThanEqualOrderByDateFromDesc(it) }

    override fun getDateFromLast() = repository.findTopByOrderByDateFromDesc()

    override fun getAllByShipmentWaybillId(id: Long?) = if (id == null) emptyList() else repository.findDistinctByLotListAllotmentListMatValueListShipmentWaybillId(id)
}