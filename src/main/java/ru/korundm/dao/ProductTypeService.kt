package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ProductType
import ru.korundm.repository.ProductTypeRepository

interface ProductTypeService : CommonService<ProductType>

@Service
@Transactional
class ProductTypeServiceImpl(
    private val repository: ProductTypeRepository
) : ProductTypeService {

    private val cl = ProductType::class.java

    override fun getAll(): List<ProductType> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductType> = repository.findAllById(idList)

    override fun save(obj: ProductType) = repository.save(obj)

    override fun saveAll(objectList: List<ProductType>): List<ProductType> = repository.saveAll(objectList)

    override fun read(id: Long): ProductType? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductType) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)
}