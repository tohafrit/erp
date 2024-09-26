package ru.korundm.dao

import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.configuration.SQLDialect
import ru.korundm.constant.BaseConstant.ONE_LONG
import ru.korundm.constant.ObjAttr
import ru.korundm.dto.prod.WarehouseStateListFirstReportProductDto
import ru.korundm.entity.*
import ru.korundm.form.search.ProductListFilterForm
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.ProductRepository
import java.time.LocalDateTime
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.*

interface ProductService : CommonService<Product> {

    fun getCountByForm(form: ProductListFilterForm): Long
    fun existsById(id: Long): Boolean
    fun getByTableDataIn(tableDataIn: TabrIn, form: ProductListFilterForm): List<Product>
    fun findLaunchNoteProductSearchTableData(input: TabrIn, launchId: Long, form: DynamicObject): TabrResultQuery<Product>
    fun findTableData(tableInput: TabrIn, form: DynamicObject): TabrResultQuery<Product>
    fun findContractTableData(tableInput: TabrIn, form: DynamicObject): TabrResultQuery<Product>
    fun getByName(name: String): List<Product>
    fun getByNameAndDecimalNumber(name: String, number: String): List<Product>
    fun findStructTableData(input: TabrIn, bomId: Long, form: DynamicObject): TabrResultQuery<Product>
    fun findWarehouseStateFirstReportTableData(input: TabrIn, form: DynamicObject): TabrResultQuery<WarehouseStateListFirstReportProductDto>
}

@Service
@Transactional
class ProductServiceImpl(
    private val repository: ProductRepository
) : ProductService {

    companion object {
        private val PRODUCT_TYPES = listOf(1L, 2L, 3L, 5L, 6L, 10L)
    }

    private val cl = Product::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<Product> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<Product> = repository.findAllById(idList)

    override fun save(obj: Product) = repository.save(obj)

    override fun saveAll(objectList: List<Product>): List<Product> = repository.saveAll(objectList)

    override fun read(id: Long): Product = repository.findById(id).orElse(null)

    override fun delete(obj: Product) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun existsById(id: Long) = repository.existsById(id)

    override fun getByName(name: String) = if (name.trim().isBlank()) emptyList() else repository.findAllByConditionalName(name)

    override fun getByNameAndDecimalNumber(name: String, number: String) =
        if (name.trim().isBlank()) emptyList() else if (number.trim().isBlank()) getByName(name) else repository.findAllByConditionalNameAndDecimalNumber(name, number)

    override fun getByTableDataIn(
        tableDataIn: TabrIn,
        form: ProductListFilterForm
    ): List<Product> {
        val cb = em.criteriaBuilder
        val criteria = cb.createQuery(cl)
        val root = criteria.from(cl)
        val predicateList = getFormPredicateList(form, root, criteria, cb)
        val select = criteria.select(root)
        select.where(*predicateList)
        if (tableDataIn.sorters.isNotEmpty()) {
            val sorter = tableDataIn.sorters[0]
            val orderExpression = when (sorter.field) {
                ObjAttr.CONDITIONAL_NAME -> root.get(Product::conditionalName.name)
                ObjAttr.TECH_SPEC_NAME -> root.get(Product::techSpecName.name)
                ObjAttr.TYPE -> root.join<Product, ProductType>(Product::type.name, JoinType.LEFT).get(ObjAttr.NAME)
                ObjAttr.DECIMAL_NUMBER -> root.get(Product::decimalNumber.name)
                ObjAttr.LETTER -> root.join<Product, ProductLetter>(Product::letter.name, JoinType.LEFT).get(ObjAttr.NAME)
                ObjAttr.POSITION -> root.get<Int>(Product::position.name)
                ObjAttr.LEAD -> root.join<Product, User>(Product::lead.name, JoinType.LEFT).get(ObjAttr.LAST_NAME)
                ObjAttr.CLASSIFICATION_GROUP -> root.join<Product, ClassificationGroup>(Product::classificationGroup.name, JoinType.LEFT).get(ClassificationGroup::number.name)
                ObjAttr.COMMENT -> root.get(Product::comment.name)
                else -> root.get<Long>(ObjAttr.ID)
            }
            criteria.orderBy(if (ASC == sorter.dir) cb.asc(orderExpression) else cb.desc(orderExpression))
        }
        val typedQuery = em.createQuery(select)
        typedQuery.firstResult = tableDataIn.start
        typedQuery.maxResults = tableDataIn.size
        return typedQuery.resultList
    }

    override fun getCountByForm(form: ProductListFilterForm): Long {
        val cb = em.criteriaBuilder
        val criteria = cb.createQuery(Long::class.java)
        val root = criteria.from(cl)
        val predicateList = getFormPredicateList(form, root, criteria, cb)
        criteria.select(cb.count(root)).where(*predicateList)
        return em.createQuery(criteria).singleResult
    }

    private fun getFormPredicateList(
        form: ProductListFilterForm,
        root: Root<Product>,
        criteria: CriteriaQuery<*>,
        cb: CriteriaBuilder
    ): Array<Predicate> {
        val predicateList = mutableListOf<Predicate>()
        val conditionalName = form.conditionalName
        if (conditionalName.isNotBlank()) predicateList.add(cb.like(root.get(Product::conditionalName.name), "%$conditionalName%"))
        val techSpecName = form.techSpecName
        if (techSpecName.isNotBlank()) predicateList.add(cb.like(root.get(Product::techSpecName.name), "%$techSpecName%"))
        val decimalNumber = form.decimalNumber
        if (decimalNumber.isNotBlank()) predicateList.add(cb.like(root.get(Product::decimalNumber.name), "%$decimalNumber%"))
        val position = form.position
        if (position.isNotBlank()) predicateList.add(cb.like(root.get<Int>(Product::position.name).`as`(String::class.java), "%$position%"))
        /*val prefix = form.prefix
        if (prefix.isNotBlank()) {
            val subquery = criteria.subquery(Long::class.java)
            val subRoot = subquery.from(Bom::class.java)
            val sapsanProductBomReferenceJoin = subRoot.join(ObjAttr.SAPSAN_PRODUCT_BOM_REFERENCE_LIST)
            val sapsanProductJoin = sapsanProductBomReferenceJoin.join(SapsanProductBomReference_.sapsanProduct)
            subquery.select(cb.literal(1L)).where(cb.and(
                cb.equal(subRoot.get<Product>(ObjAttr.PRODUCT), root.get<Long>(ObjAttr.ID)),
                cb.like(sapsanProductJoin.get(ObjAttr.PREFIX), "%$prefix%")
            ))
            predicateList.add(cb.exists(subquery))
        }*/
        if (form.descriptor != null) {
            val bomJoin = root.join<Product, Bom>(Product::bomList.name, JoinType.LEFT)
            predicateList.add(cb.equal(bomJoin.get<Long>(ObjAttr.DESCRIPTOR), form.descriptor))
            // Значение версии должно быть больше ноля
            predicateList.add(cb.notEqual(bomJoin.get<Int>(ObjAttr.MAJOR), 0))
        }
        val comment = form.comment
        if (comment.isNotBlank()) predicateList.add(cb.like(root.get(Product::comment.name), "%$comment%"))
        val typeIdList = form.typeIdList
        if (typeIdList.isNotEmpty()) {
            predicateList.add(cb.and(root.get<ProductType>(Product::type.name).get<Long>(ObjAttr.ID).`in`(typeIdList)))
        } else {
            predicateList.add(cb.and(root.get<ProductType>(Product::type.name).get<Long>(ObjAttr.ID).`in`(PRODUCT_TYPES)))
        }
        val letterIdList = form.letterIdList
        if (letterIdList.isNotEmpty()) predicateList.add(cb.and(root.get<ProductLetter>(Product::letter.name).get<Long>(ObjAttr.ID).`in`(letterIdList)))
        val leadIdList = form.leadIdList
        if (leadIdList.isNotEmpty()) predicateList.add(cb.and(root.get<User>(Product::lead.name).get<Long>(ObjAttr.ID).`in`(leadIdList)))
        val classificationGroupIdList = form.classificationGroupIdList
        if (classificationGroupIdList.isNotEmpty()) {
            predicateList.add(cb.and(root.get<ClassificationGroup>(Product::classificationGroup.name).get<Long>(ObjAttr.ID).`in`(classificationGroupIdList)))
        }
        val excludeProductIdList = form.excludeProductIdList
        if (excludeProductIdList.isNotEmpty()) predicateList.add(cb.not(root.`in`(excludeProductIdList)))
        val excludeProductTypeIdList = form.excludeProductTypeIdList
        if (excludeProductTypeIdList.isNotEmpty()) predicateList.add(cb.not(root.get<ProductType>(ObjAttr.TYPE).`in`(excludeProductTypeIdList)))
        if (form.archive == form.active) {
            if (!form.archive) predicateList.add(cb.isNull(root.get<LocalDateTime>(Product::archiveDate.name)))
            if (!form.active) predicateList.add(cb.isNotNull(root.get<LocalDateTime>(Product::archiveDate.name)))
        } else {
            predicateList.add(if (form.active) cb.isNull(root.get<LocalDateTime>(Product::archiveDate.name)) else cb.isNotNull(root.get<LocalDateTime>(Product::archiveDate.name)))
        }
        form.serial?.let { predicateList += cb.equal(root.get<Boolean>(Product::serial.name), it) }
        return predicateList.toTypedArray()
    }

    override fun findLaunchNoteProductSearchTableData(
        input: TabrIn,
        launchId: Long,
        form: DynamicObject
    ): TabrResultQuery<Product> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.CONDITIONAL_NAME -> prSort(root.get<String>(Product::conditionalName.name))
                else -> prSort(root.get<Long>(ObjAttr.ID))
            }
            cq.orderBy(orderList)
        }
        val subQuery = cq.subquery(Long::class.java)
        val subRoot = subQuery.from(LaunchProduct::class.java)
        subQuery.select(cb.literal(ONE_LONG)).where(
            cb.equal(subRoot.get<Product>(LaunchProduct::product.name), root.get<Long>(ObjAttr.ID)),
            cb.equal(subRoot.get<Launch>(LaunchProduct::launch.name), launchId)
        )
        //
        val predicateList = mutableListOf<Predicate>()
        val productName = form.stringNotNull(ObjAttr.PRODUCT_NAME)
        if (productName.isNotBlank()) predicateList += cb.like(root.get(Product::conditionalName.name), "%$productName%")
        select.where(cb.not(cb.exists(subQuery)), *predicateList.toTypedArray())
        val tq = em.createQuery(select)
        tq.firstResult = input.start
        tq.maxResults = input.size
        val resultList = tq.resultList
        return TabrResultQuery.instance(resultList)
    }

    override fun findContractTableData(
        tableInput: TabrIn,
        form: DynamicObject
    ) : TabrResultQuery<Product> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        select.where(*filterPredicateList(form, root, cb))
        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.CONDITIONAL_NAME -> root.get<String>(Product::conditionalName.name)
                ObjAttr.NAME -> prSort(root.get<ProductType>(Product::type.name).get<String>(ProductType::name.name))
                ObjAttr.DECIMAL_NUMBER -> prSort(root.get<Int>(Product::decimalNumber.name))
                ObjAttr.COMMENT -> prSort(root.get<String>(Product::comment.name))
                else -> prSort(root.get<Long>(ObjAttr.ID))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select)
        typedQuery.firstResult = tableInput.start
        typedQuery.maxResults = tableInput.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    private fun filterPredicateList(
        form: DynamicObject,
        root: Root<Product>,
        cb: CriteriaBuilder
    ): Array<Predicate> {
        val predicateList = mutableListOf<Predicate>()
        form.string(ObjAttr.PRODUCT_NAME)?.let { predicateList += cb.like(root.get(Product::conditionalName.name), "%$it%") }
        form.string(ObjAttr.DECIMAL_NUMBER)?.let { predicateList += cb.like(root.get(Product::decimalNumber.name), "%$it%") }
        form.string(ObjAttr.COMMENT)?.let { predicateList += cb.like(root.get(ObjAttr.COMMENT), "%$it%") }
        val typeIdList = form.listLong(ObjAttr.TYPE_ID_LIST).ifEmpty { PRODUCT_TYPES }
        predicateList += cb.and(root.get<Long>(ObjAttr.TYPE).`in`(typeIdList))
        return predicateList.toTypedArray()
    }

    override fun findTableData(
        tableInput: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<Product> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        cq.where(*filterPredicateList(root, cb, cq, form))
        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.CONDITIONAL_NAME -> prSort(root.get<String>(Product::conditionalName.name))
                ObjAttr.TECH_SPEC_NAME -> prSort(root.get<String>(Product::techSpecName.name))
                ObjAttr.TYPE -> prSort(root.join<Product, ProductType>(Product::type.name, JoinType.LEFT).get<String>(ObjAttr.NAME))
                ObjAttr.DECIMAL_NUMBER -> prSort(root.get<String>(Product::decimalNumber.name))
                ObjAttr.LETTER -> prSort(root.join<Product, ProductLetter>(Product::letter.name, JoinType.LEFT).get<String>(ObjAttr.NAME))
                ObjAttr.POSITION -> prSort(root.get<Int>(Product::position.name))
                ObjAttr.LEAD -> prSort(root.join<Product, User>(Product::lead.name, JoinType.LEFT).get<String>(ObjAttr.LAST_NAME))
                ObjAttr.CLASSIFICATION_GROUP -> prSort(root.join<Product, ClassificationGroup>(Product::classificationGroup.name, JoinType.LEFT).get<Int>(ClassificationGroup::number.name))
                ObjAttr.COMMENT -> prSort(root.get<String>(Product::comment.name))
                else -> prSort(root.get<Long>(ObjAttr.ID))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select)
        typedQuery.firstResult = tableInput.start
        typedQuery.maxResults = tableInput.size
        val resultList = typedQuery.resultList
        return TabrResultQuery.instance(resultList)
    }

    private fun filterPredicateList(
        root: Root<Product>,
        cb: CriteriaBuilder,
        criteria: CriteriaQuery<*>,
        form: DynamicObject
    ): Array<Predicate> {
        val predicateList = mutableListOf<Predicate>()

        val conditionalName = form.string(ObjAttr.CONDITIONAL_NAME)
        if (!conditionalName.isNullOrBlank()) predicateList += cb.like(root.get(Product::conditionalName.name), "%$conditionalName%")

        val techSpecName = form.string(ObjAttr.TECH_SPEC_NAME)
        if (!techSpecName.isNullOrBlank()) predicateList += cb.like(root.get(Product::techSpecName.name), "%$techSpecName%")

        val decimalNumber = form.string(ObjAttr.DECIMAL_NUMBER)
        if (!decimalNumber.isNullOrBlank()) predicateList += cb.like(root.get(Product::decimalNumber.name), "%$decimalNumber%")

        val position = form.string(ObjAttr.POSITION)
        if (!position.isNullOrBlank()) predicateList += cb.like(root.get<Int>(Product::position.name).`as`(String::class.java), "%$position%")

        val excludeIdList = form.listLong(ObjAttr.PRODUCT_APPLICABILITY_ID_LIST)
        if (excludeIdList.isNotEmpty()) predicateList += cb.not(root.get<Long>(Product::id.name).`in`(excludeIdList))

        /*val prefix = form.string(ObjAttr.PREFIX)
        if (!prefix.isNullOrBlank()) {
            val subquery = criteria.subquery(Long::class.java)
            val subRoot = subquery.from(Bom::class.java)
            val sapsanProductBomReferenceJoin = subRoot.join(ObjAttr.SAPSAN_PRODUCT_BOM_REFERENCE_LIST)
            val sapsanProductJoin = sapsanProductBomReferenceJoin.join(SapsanProductBomReference_.sapsanProduct)
            subquery.select(cb.literal(1L)).where(cb.and(
                cb.equal(subRoot.get<Product>(ObjAttr.PRODUCT), root.get<Long>(ObjAttr.ID)),
                cb.like(sapsanProductJoin.get(ObjAttr.PREFIX), "%$prefix%")
            ))
            predicateList.add(cb.exists(subquery))
        }*/

        form.int(ObjAttr.DESCRIPTOR)?.let {
            val bomJoin = root.join<Product, Bom>(ObjAttr.BOM_LIST, JoinType.LEFT)
            predicateList.add(cb.equal(bomJoin.get<Long>(ObjAttr.DESCRIPTOR), it))
            // Значение версии должно быть больше ноля
            predicateList.add(cb.notEqual(bomJoin.get<Int>(ObjAttr.MAJOR), 0))
        }

        val comment = form.string(ObjAttr.COMMENT)
        if (!decimalNumber.isNullOrBlank()) predicateList += cb.like(root.get(Product::comment.name), "%$comment%")

        val typeIdList = form.listLong(ObjAttr.TYPE_ID_LIST)
        predicateList += cb.and(root.get<ProductType>(Product::type.name).get<Long>(ObjAttr.ID).`in`(typeIdList.ifEmpty { PRODUCT_TYPES }))

        val letterIdList = form.listLong(ObjAttr.LETTER_ID_LIST)
        if (letterIdList.isNotEmpty()) predicateList += cb.and(root.get<ProductLetter>(Product::letter.name).get<Long>(ObjAttr.ID).`in`(letterIdList))

        val leadIdList = form.listLong(ObjAttr.LEAD_ID_LIST)
        if (leadIdList.isNotEmpty()) predicateList += cb.and(root.get<User>(Product::lead.name).get<Long>(ObjAttr.ID).`in`(leadIdList))

        val classificationGroupIdList = form.listLong(ObjAttr.CLASSIFICATION_GROUP_ID_LIST)
        if (classificationGroupIdList.isNotEmpty()) {
            predicateList += cb.and(root.get<ClassificationGroup>(Product::classificationGroup.name).get<Long>(ObjAttr.ID).`in`(classificationGroupIdList))
        }

        val excludeProductIdList = form.listLong(ObjAttr.EXCLUDE_PRODUCT_ID_LIST)
        if (excludeProductIdList.isNotEmpty()) predicateList += cb.not(root.`in`(excludeProductIdList))

        val excludeProductTypeIdList = form.listLong(ObjAttr.EXCLUDE_PRODUCT_TYPE_ID_LIST)
        if (excludeProductTypeIdList.isNotEmpty()) predicateList += cb.not(root.get<ProductType>(Product::type.name).`in`(excludeProductTypeIdList))

        val active = form.boolNotNull(ObjAttr.ACTIVE)
        val archive = form.boolNotNull(ObjAttr.ARCHIVE)
        if (active != archive) {
            predicateList.add(if (active) cb.isNull(root.get<LocalDateTime>(Product::archiveDate.name)) else cb.isNotNull(root.get<LocalDateTime>(Product::archiveDate.name)))
        }
        return predicateList.toTypedArray()
    }

    override fun findStructTableData(input: TabrIn, bomId: Long, form: DynamicObject): TabrResultQuery<Product> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)

        val predicateList = mutableListOf<Predicate>()
        val productName = form.stringNotNull(ObjAttr.CONDITIONAL_NAME)
        if (productName.isNotBlank()) predicateList += cb.like(root.get(Product::conditionalName.name), "%$productName%")

        val subBomQuery = cq.subquery(Long::class.java)
        val subBomRoot = subBomQuery.from(Bom::class.java)
        val subBomPredicateList = mutableListOf<Predicate>()
        subBomPredicateList += cb.equal(root.get<Long>(ObjAttr.ID), subBomRoot.get<Product>(Bom::product.name))
        subBomPredicateList += cb.equal(subBomRoot.get<Long>(ObjAttr.ID), bomId)
        predicateList += cb.exists(subBomQuery.select(cb.literal(ONE_LONG)).where(*subBomPredicateList.toTypedArray())).not()

        val subBsiQuery = cq.subquery(Long::class.java)
        val subBsiRoot = subBsiQuery.from(BomSpecItem::class.java)
        val subBsiPredicateList = mutableListOf<Predicate>()
        subBsiPredicateList += cb.equal(root.get<Long>(ObjAttr.ID), subBsiRoot.get<Product>(ObjAttr.PRODUCT))
        subBsiPredicateList += cb.equal(subBsiRoot.get<Bom>(ObjAttr.BOM), bomId)
        predicateList += cb.exists(subBsiQuery.select(cb.literal(ONE_LONG)).where(*subBsiPredicateList.toTypedArray())).not()

        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.CONDITIONAL_NAME -> prSort(root.get<String>(Product::conditionalName.name))
                else -> prSort(root.get<Long>(ObjAttr.ID))
            }
            cq.orderBy(orderList)
        }

        val typedQuery = em.createQuery(cq.select(root).where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun findWarehouseStateFirstReportTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<WarehouseStateListFirstReportProductDto> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(WarehouseStateListFirstReportProductDto::class.java)
        val root = cq.from(cl)

        val predicateList = mutableListOf<Predicate>()
        val productName = form.stringNotNull(ObjAttr.PRODUCT)
        if (productName.isNotBlank()) predicateList += cb.like(root.get(Product::conditionalName.name), "%$productName%")
        form.long(ObjAttr.TYPE_ID)?.let { predicateList += cb.equal(root.get<ProductType>(Product::type.name), it) }
        form.bool(ObjAttr.SERIAL)?.let { predicateList += cb.equal(root.get<Boolean>(Product::serial.name), it) }

        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.PRODUCT -> prSort(root.get<String>(Product::conditionalName.name))
                ObjAttr.TYPE -> prSort(root.get<ProductType>(Product::type.name).get<String>(ProductType::name.name))
                else -> prSort(root.get<Long>(ObjAttr.ID))
            }
            cq.orderBy(orderList)
        }

        val typedQuery = em.createQuery(cq.multiselect(
            root.get<Long>(Product::id.name),
            root.get<String>(Product::conditionalName.name),
            root.get<ProductType>(Product::type.name).get<String>(ProductType::name.name),
            root.get<Boolean>(Product::serial.name),
            cb.function(SQLDialect.Function.COUNT_OVER, Long::class.java)
        ).where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }
}