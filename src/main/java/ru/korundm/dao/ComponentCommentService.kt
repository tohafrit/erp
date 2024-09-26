package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.Component
import ru.korundm.entity.ComponentComment
import ru.korundm.entity.User
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.ComponentCommentRepository
import java.time.LocalDateTime
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface ComponentCommentService : CommonService<ComponentComment> {

    fun findTableData(input: TabrIn, form: DynamicObject, componentId: Long): TabrResultQuery<ComponentComment>
}

@Service
@Transactional
class ComponentCommentServiceImpl(
    private val repository: ComponentCommentRepository
) : ComponentCommentService {

    private val cl = ComponentComment::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<ComponentComment> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<ComponentComment> = repository.findAllById(idList)

    override fun save(obj: ComponentComment): ComponentComment {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<ComponentComment>): List<ComponentComment> = repository.saveAll(objectList)

    override fun read(id: Long): ComponentComment? = repository.findById(id).orElse(null)

    override fun delete(obj: ComponentComment) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findTableData(input: TabrIn, form: DynamicObject, componentId: Long): TabrResultQuery<ComponentComment> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        val predicateList = mutableListOf<Predicate>()
        predicateList += cb.equal(root.get<Component>(ComponentComment::component.name), componentId)

        form.long(ObjAttr.CREATED_BY)?.let { predicateList += cb.equal(root.get<User>(ComponentComment::user.name), it) }
        val comment = form.stringNotNull(ObjAttr.COMMENT)
        if (comment.isNotBlank()) predicateList += cb.like(root.get(ComponentComment::comment.name), "%$comment%")
        val pathCreateDate: Path<LocalDateTime> = root.get(ComponentComment::createDatetime.name)
        form.date(ObjAttr.CREATE_DATE_FROM)?.let { predicateList += cb.greaterThanOrEqualTo(pathCreateDate, it.atStartOfDay()) }
        form.date(ObjAttr.CREATE_DATE_TO)?.let { predicateList += cb.lessThanOrEqualTo(pathCreateDate, it.atStartOfDay()) }

        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.CREATED_BY -> prSort(root.get<User>(ComponentComment::user.name))
                ObjAttr.CREATE_DATE -> prSort(root.get<LocalDateTime>(ComponentComment::createDatetime.name))
                ObjAttr.COMMENT -> prSort(root.get<String>(ComponentComment::comment.name))
                else -> prSort(root.get<Long>(ObjAttr.ID))
            }
            cq.orderBy(orderList)
        }

        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}