package ru.korundm.dao.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.dao.ProductChargesProtocolService
import ru.korundm.entity.ProductChargesProtocol
import ru.korundm.repository.ProductChargesProtocolRepository

@Service
@Transactional
class ProductChargesProtocolServiceImpl(
    private val productChargesProtocolRepository: ProductChargesProtocolRepository
) : ProductChargesProtocolService {

    override fun getAll(): List<ProductChargesProtocol> = productChargesProtocolRepository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductChargesProtocol> = productChargesProtocolRepository.findAllById(idList)

    override fun save(obj: ProductChargesProtocol) = productChargesProtocolRepository.save(obj)

    override fun saveAll(objectList: List<ProductChargesProtocol>): List<ProductChargesProtocol> = productChargesProtocolRepository.saveAll(objectList)

    override fun read(id: Long): ProductChargesProtocol? = productChargesProtocolRepository.findById(id).orElse(null)

    override fun delete(obj: ProductChargesProtocol) = productChargesProtocolRepository.delete(obj)

    override fun deleteById(id: Long) = productChargesProtocolRepository.deleteById(id)
}