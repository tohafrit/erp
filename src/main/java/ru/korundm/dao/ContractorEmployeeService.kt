package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ContractorEmployee
import ru.korundm.repository.ContractorEmployeeRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ContractorEmployeeService : CommonService<ContractorEmployee>

@Service
@Transactional
class ContractorEmployeeServiceImpl(
    private val repository: ContractorEmployeeRepository
) : ContractorEmployeeService {

    private val cl = ContractorEmployee::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ContractorEmployee> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ContractorEmployee> = repository.findAllById(idList)

    override fun save(obj: ContractorEmployee): ContractorEmployee {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ContractorEmployee>): List<ContractorEmployee> = repository.saveAll(objectList)

    override fun read(id: Long): ContractorEmployee? = repository.findById(id).orElse(null)

    override fun delete(obj: ContractorEmployee) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)
}