package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ProductLetter
import ru.korundm.repository.ProductLetterRepository

interface ProductLetterService : CommonService<ProductLetter> {

    fun findByName(name: String): ProductLetter?
}

@Service
@Transactional
class ProductLetterServiceImpl(
    private val repository: ProductLetterRepository
) : ProductLetterService {

    override fun getAll(): List<ProductLetter> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductLetter> = repository.findAllById(idList)

    override fun save(obj: ProductLetter) = repository.save(obj)

    override fun saveAll(objectList: List<ProductLetter>): List<ProductLetter> = repository.saveAll(objectList)

    override fun read(id: Long): ProductLetter = repository.findById(id).orElse(null)

    override fun delete(obj: ProductLetter) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findByName(name: String) = repository.findFirstByName(name)
}
