package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.Product
import ru.korundm.entity.ProductDocumentation
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.ProductDocumentationRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path

interface ProductDocumentationService : CommonService<ProductDocumentation> {
    fun findTableData(input: TabrIn, productId: Long): TabrResultQuery<ProductDocumentation>
}

@Service
@Transactional
class ProductDocumentationServiceImpl(
    private val repository: ProductDocumentationRepository
) : ProductDocumentationService {

    private val cl = ProductDocumentation::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ProductDocumentation> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ProductDocumentation> = repository.findAllById(idList)

    override fun save(obj: ProductDocumentation): ProductDocumentation {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ProductDocumentation>): List<ProductDocumentation> = repository.saveAll(objectList)

    override fun read(id: Long): ProductDocumentation? = repository.findById(id).orElse(null)

    override fun delete(obj: ProductDocumentation) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(input: TabrIn, productId: Long): TabrResultQuery<ProductDocumentation> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        select.where(cb.equal(root.get<Product>(ProductDocumentation::product.name), productId))
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.NAME -> prSort(root.get<String>(ProductDocumentation::name.name))
                ObjAttr.COMMENT -> prSort(root.get<String>(ProductDocumentation::comment.name))
                else -> prSort(root.get<Long>(ProductDocumentation::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select)
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        val resultList = typedQuery.resultList
        return TabrResultQuery.instance(resultList)
    }
}