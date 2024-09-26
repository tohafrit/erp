package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ProductDeciphermentType
import ru.korundm.repository.ProductDeciphermentTypeRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ProductDeciphermentTypeService : CommonService<ProductDeciphermentType>

@Service
@Transactional
class ProductDeciphermentTypeServiceImpl(
    private val repository: ProductDeciphermentTypeRepository
) : ProductDeciphermentTypeService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductDeciphermentType> = repository.findAll(Sort.by(Sort.Direction.ASC, ProductDeciphermentType::sort.name))

    override fun getAllById(idList: List<Long>): List<ProductDeciphermentType> = repository.findAllById(idList)

    override fun save(obj: ProductDeciphermentType): ProductDeciphermentType {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductDeciphermentType>): List<ProductDeciphermentType> = repository.saveAll(objectList)

    override fun read(id: Long): ProductDeciphermentType? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductDeciphermentType) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)
}