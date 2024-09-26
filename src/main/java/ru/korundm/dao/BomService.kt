package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.Bom
import ru.korundm.entity.BomAttribute
import ru.korundm.entity.Launch
import ru.korundm.entity.Product
import ru.korundm.form.search.ProductOccurrenceFilterForm
import ru.korundm.repository.BomRepository
import ru.korundm.util.KtCommonUtil.typedManyResult
import ru.korundm.util.KtCommonUtil.typedSingleResult
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface BomService : CommonService<Bom> {

    fun getAllByProductId(productId: Long?, sort: Sort): List<Bom>
    fun getAllSortedVersionByProductId(productId: Long?): List<Bom>
    fun getForProductOccurrence(form: ProductOccurrenceFilterForm, productId: Long): List<Bom>
    fun getAllSortedVersionDescByProductId(productId: Long?): List<Bom>
    fun maxDescriptor(): Long
    fun getApprovedToLastLaunch(productId: Long?): Bom?
    fun getLastApprovedOrAccepted(productId: Long?): Bom?
    fun getLastByProductId(productId: Long?): Bom?
}

@Service
@Transactional
class BomServiceImpl(
    private val repository: BomRepository
) : BomService {

    private val cl = Bom::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<Bom> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<Bom> = repository.findAllById(idList)

    override fun save(obj: Bom): Bom {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<Bom>): List<Bom> = repository.saveAll(objectList)

    override fun read(id: Long): Bom? = repository.findById(id).orElse(null)

    override fun delete(obj: Bom) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun getAllByProductId(productId: Long?, sort: Sort) = if (productId == null) emptyList() else repository.findAllByProductIdAndMajorNot(productId, 0, sort)

    override fun getAllSortedVersionByProductId(productId: Long?) = getAllByProductId(productId, Sort.by(Sort.Direction.ASC, Bom::major.name, Bom::minor.name, Bom::modification.name))

    override fun getAllSortedVersionDescByProductId(productId: Long?) = getAllByProductId(productId, Sort.by(Sort.Direction.DESC, Bom::major.name, Bom::minor.name, Bom::modification.name))

    override fun getForProductOccurrence(form: ProductOccurrenceFilterForm, productId: Long): List<Bom> {
        val nativeQuery = """
            SELECT
               b.*
            FROM
               bom_spec_items bsi
               JOIN
               boms b
               ON
               b.id = bsi.bom_id
            
               JOIN
               products p
               ON
               p.id = b.product_id
            
               JOIN (
                   SELECT
                       p.id product_id,
                       b.id bom_id,
                       RANK() OVER(PARTITION BY p.id ORDER BY b.major DESC, b.minor DESC, b.modification DESC) rnk
                   FROM
                       products p
                       JOIN
                       boms b
                       ON
                       b.product_id = p.id
               ) last_bom
               ON
               last_bom.product_id = p.id
               AND last_bom.rnk = 1
            
               LEFT JOIN (
                   SELECT
                       p.id product_id,
                       b.id bom_id,
                       RANK() OVER(PARTITION BY p.id ORDER BY ba.approve_date DESC) rnk
                   FROM
                       products p
                       JOIN
                       boms b
                       ON
                       b.product_id = p.id
            
                       JOIN
                       bom_attributes ba
                       ON
                       b.id = ba.bom_id
                   WHERE
                       ba.approve_date IS NOT NULL
               ) last_approved
               ON
               last_approved.product_id = b.product_id
               AND last_approved.rnk = 1
            
               LEFT JOIN (
                   SELECT
                       p.id product_id,
                       b.id bom_id,
                       RANK() OVER(PARTITION BY p.id ORDER BY ba.accept_date DESC) rnk
                   FROM
                       products p
                       JOIN
                       boms b
                       ON
                       b.product_id = p.id
            
                       JOIN
                       bom_attributes ba
                       ON
                       b.id = ba.bom_id
                   WHERE
                       ba.accept_date IS NOT NULL
               ) last_accepted
               ON
               last_accepted.product_id = b.product_id
               AND last_accepted.rnk = 1
            WHERE
               bsi.product_id = :productId
               AND (
                   (:active = 1 AND p.archive_date IS NULL)
                   OR 
                   (:archive = 1 AND p.archive_date IS NOT NULL)
               )
               AND (:lastApprove <> 1 OR :lastApprove = 1 AND last_approved.bom_id = b.id)
               AND (:lastAccept <> 1 OR :lastAccept = 1 AND last_accepted.bom_id = b.id)
               AND (:lastNumber <> 1 OR :lastNumber = 1 AND last_bom.bom_id = b.id)
        """.trimIndent()
        val query = em.createNativeQuery(nativeQuery, Bom::class.java)
        query.setParameter("productId", productId)
        query.setParameter("active", form.isActive)
        query.setParameter("archive", form.isArchive)
        query.setParameter("lastApprove", form.isLastApprove)
        query.setParameter("lastAccept", form.isLastAccept)
        query.setParameter("lastNumber", form.isLastNumber)
        return query.typedManyResult()
    }

    override fun maxDescriptor() = repository.maxDescriptor() ?: 0

    override fun getApprovedToLastLaunch(productId: Long?): Bom? {
        if (productId == null) return null
        val cb = em.criteriaBuilder
        val c = cb.createQuery(cl)
        val bom = c.from(cl)
        val attributeJoin = bom.join<Bom, BomAttribute>(Bom::bomAttributeList.name)
        val launchJoin = attributeJoin.join<BomAttribute, Launch>(ObjAttr.LAUNCH)
        c.orderBy(cb.desc(launchJoin.get<Int>(Launch::year.name)), cb.desc(launchJoin.get<Int>(Launch::number.name)))
        val select = c.select(bom).where(
            cb.equal(bom.get<Product>(Bom::product.name), productId),
            cb.isNotNull(attributeJoin.get<LocalDate>(ObjAttr.APPROVE_DATE))
        )
        val query = em.createQuery(select)
        query.firstResult = 0
        query.maxResults = 1
        return query.typedSingleResult()
    }

    override fun getLastApprovedOrAccepted(productId: Long?): Bom? {
        if (productId == null) return null
        val cb = em.criteriaBuilder
        val c = cb.createQuery(cl)
        val bom = c.from(cl)
        val attributeJoin = bom.join<Bom, BomAttribute>(Bom::bomAttributeList.name)
        c.orderBy(
            cb.desc(attributeJoin.get<LocalDate>(ObjAttr.APPROVE_DATE)),
            cb.desc(attributeJoin.get<LocalDate>(ObjAttr.ACCEPT_DATE))
        )
        val select = c.select(bom).where(
            cb.equal(bom.get<Product>(Bom::product.name), productId),
            cb.or(
                attributeJoin.get<LocalDate>(ObjAttr.APPROVE_DATE).isNotNull,
                attributeJoin.get<LocalDate>(ObjAttr.ACCEPT_DATE).isNotNull
            )
        )
        val query = em.createQuery(select)
        query.firstResult = 0
        query.maxResults = 1
        return query.typedSingleResult()
    }

    override fun getLastByProductId(productId: Long?) = if (productId == null) null else repository.findFirstByProductIdOrderByMajorDescMinorDescModificationDesc(productId)
}