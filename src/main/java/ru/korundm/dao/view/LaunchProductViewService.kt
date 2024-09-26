package ru.korundm.dao.view

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.view.LaunchProductView
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.view.LaunchProductViewRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface LaunchProductViewService : CommonViewService<LaunchProductView> {

    fun findTableData(input: TabrIn, launchId: Long, form: DynamicObject): TabrResultQuery<LaunchProductView>
}

@Service
@Transactional(readOnly = true)
class LaunchProductViewImpl(
    private val repository: LaunchProductViewRepository
) : LaunchProductViewService {

    companion object {
        const val PRETENDER_TYPE = 1
        const val LAUNCHED_TYPE = 2
    }

    private val cl = LaunchProductView::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<LaunchProductView> = repository.findAll()

    override fun getAllByIdList(idList: List<Long>): List<LaunchProductView> = repository.findAllById(idList)

    override fun read(id: Long): LaunchProductView = repository.findById(id).orElse(null)

    override fun findTableData(input: TabrIn, launchId: Long, form: DynamicObject): TabrResultQuery<LaunchProductView> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        val predicateList = mutableListOf<Predicate>()
        predicateList += cb.equal(root.get<Long>(LaunchProductView::launchId.name), launchId)
        // ЭВМ, Модуль, Периферийное устройство, Составная часть ЭВМ, Макетные образцы и оснастка, Материалы и комплектующие
        predicateList += root.get<Long>(LaunchProductView::productTypeId.name).`in`(1, 2, 3, 5, 6, 10)
        form.string(ObjAttr.PRODUCT_NAME)?.let { predicateList += cb.like(root.get(LaunchProductView::productName.name), "%$it%") }
        form.intNotNull(ObjAttr.TYPE).let {
            if (it == PRETENDER_TYPE) {
                predicateList += cb.equal(root.get<Boolean>(LaunchProductView::hasPretender.name), true)
            } else if (it == LAUNCHED_TYPE) {
                predicateList += cb.equal(root.get<Boolean>(LaunchProductView::hasPretender.name), false)
            }
            predicateList += cb.equal(root.get<Boolean>(LaunchProductView::isLaunched.name), true)
        }
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.PRODUCT_NAME -> prSort(root.get<String>(LaunchProductView::productName.name))
                else -> prSort(root.get<Long>(LaunchProductView::productId.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}