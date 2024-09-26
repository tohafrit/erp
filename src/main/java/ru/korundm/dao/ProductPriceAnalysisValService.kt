package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.ProductPriceAnalysisVal
import ru.korundm.repository.ProductPriceAnalysisValRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface ProductPriceAnalysisValService : CommonService<ProductPriceAnalysisVal> {

    fun findFirstByPeriodId(periodId: Long): ProductPriceAnalysisVal?
    fun deleteAllByPeriodId(periodId: Long)
}

@Service
@Transactional
class ProductPriceAnalysisValServiceImpl(
    private val repository: ProductPriceAnalysisValRepository
) : ProductPriceAnalysisValService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun findFirstByPeriodId(periodId: Long): ProductPriceAnalysisVal? = repository.findFirstByPeriodId(periodId)

    override fun deleteAllByPeriodId(periodId: Long) = repository.deleteAllByPeriodId(periodId)

    override fun getAll(): List<ProductPriceAnalysisVal> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductPriceAnalysisVal> = repository.findAllById(idList)

    override fun save(obj: ProductPriceAnalysisVal): ProductPriceAnalysisVal {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductPriceAnalysisVal>): List<ProductPriceAnalysisVal> = repository.saveAll(objectList)

    override fun read(id: Long): ProductPriceAnalysisVal? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductPriceAnalysisVal) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)
}