package ru.korundm.dao

import org.hibernate.exception.ConstraintViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.PersistenceException
import kotlin.reflect.KClass

interface BaseService {

    fun exec(expr: (em: EntityManager) -> Unit)
    fun existsRelation(cl: KClass<*>, id: Long?): Boolean
}

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
class BaseServiceImpl : BaseService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun exec(expr: (em: EntityManager) -> Unit) = expr(em)

    override fun existsRelation(cl: KClass<*>, id: Long?): Boolean {
        if (id == null) return false
        em.clear()
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
        try {
            em.remove(em.find(cl.java, id))
            em.flush()
        } catch (e: PersistenceException) {
            if (e.cause is ConstraintViolationException) return true else throw e
        }
        return false
    }
}