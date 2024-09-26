package ru.korundm.dao

import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.BaseConstant.ZERO_INT
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.*
import ru.korundm.enumeration.ContractType
import ru.korundm.enumeration.Performer
import ru.korundm.form.LetterContractFilterForm
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.ContractRepository
import java.time.LocalDateTime
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.TypedQuery
import javax.persistence.criteria.*

interface ContractService : CommonService<Contract> {

    fun queryDataByFilterForm(tableDataIn: TabrIn, form: LetterContractFilterForm): TabrResultQuery<Contract>
    fun getByParams(performer: Long, contractNumber: Long, contractType: Long, year: Int): List<Contract>
    fun findLastContract(): Contract?
    fun findTableData(tableInput: TabrIn, form: DynamicObject): TabrResultQuery<Contract>
}

@Service
@Transactional
class ContractServiceImpl(
    private val contractRepository: ContractRepository
) : ContractService {

    private val cl = Contract::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<Contract> = contractRepository.findAll()

    override fun getAllById(idList: List<Long>): List<Contract> = contractRepository.findAllById(idList)

    override fun save(obj: Contract) = contractRepository.save(obj)

    override fun saveAll(objectList: List<Contract>): List<Contract> = contractRepository.saveAll(objectList)

    override fun read(id: Long): Contract? = contractRepository.findById(id).orElse(null)

    override fun delete(obj: Contract) = contractRepository.delete(obj)

    override fun deleteById(id: Long) = contractRepository.deleteById(id)

    override fun findLastContract(): Contract? {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val sectionJoin = root.join<Contract, ContractSection>(Contract::sectionList.name, JoinType.INNER)
        sectionJoin.on(cb.equal(sectionJoin.get<Long>(ContractSection::number.name), ZERO_INT))
        return em.createQuery(cq.select(root).orderBy(
            cb.desc(sectionJoin.get<Int>(ContractSection::year.name)),
            cb.desc(root.get<Int>(Contract::number.name))
        )).setMaxResults(ONE_INT).resultList.singleOrNull()
    }

    override fun getByParams(
        performer: Long,
        contractNumber: Long,
        contractType: Long,
        year: Int
    ): List<Contract> {
        val cb: CriteriaBuilder = em.criteriaBuilder
        val criteria = cb.createQuery(cl)
        val root = criteria.from(cl)
        val predicateList = mutableListOf<Predicate>()
        predicateList.add(cb.equal(root.get<Long>(Contract::type.name), contractType))
        predicateList.add(cb.equal(root.get<Long>(Contract::performer.name), performer))
        predicateList.add(cb.equal(root.get<Long>(Contract::number.name), contractNumber))

        // Ограничение по году
        val contractSectionJoin = root.join<Contract, ContractSection>(Contract::sectionList.name, JoinType.INNER)
        predicateList.add(cb.equal(contractSectionJoin.get<Long>(ContractSection::number.name), 0L))
        predicateList.add(cb.equal(contractSectionJoin.get<Int>(ContractSection::year.name), year))
        val select = criteria.select(root).distinct(true)
        select.where(*predicateList.toTypedArray())
        val typedQuery: TypedQuery<Contract> = em.createQuery(select)
        return typedQuery.resultList
    }

    override fun findTableData(
        tableInput: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<Contract> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        select.where(*filterPredicateList(form, root, cb))
        val sectionJoin = root.join<Contract, ContractSection>(Contract::sectionList.name, JoinType.INNER)
        sectionJoin.on(cb.equal(sectionJoin.get<Long>(ContractSection::number.name), 0))
        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                Contract::fullNumber.name -> {
                    prSort(sectionJoin.get<Int>(ContractSection::year.name))
                    prSort(root.get<Int>(Contract::number.name))
                }
                Company::shortName.name -> prSort(root.get<String>(Company::shortName.name))
                Company::location.name -> prSort(root.get<String>(Company::location.name))
                else -> prSort(root.get<Long>(Contract::id.name))
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
        form: DynamicObject,
        root: Root<Contract>,
        cb: CriteriaBuilder
    ): Array<Predicate> {
        val predicateList = mutableListOf<Predicate>()
        predicateList += cb.equal(root.get<Long>(Contract::performer.name), Performer.OAOKORUND.id)
        predicateList += cb.notEqual(root.get<ContractType>(Contract::type.name), ContractType.OFFICIAL_MEMO)
        val customerJoin = root.join<Contract, Company>(Contract::customer.name, JoinType.LEFT)
        form.long(ObjAttr.NUMBER)?.let { predicateList += cb.equal(root.get<Long>(Contract::number.name), it) }
        form.string(ObjAttr.CUSTOMER)?.let { predicateList += cb.like(customerJoin[Company::name.name], "%$it%") }
        val typeIdList = form.listLong(ObjAttr.TYPE_ID_LIST)
        predicateList += if (typeIdList.isNotEmpty()) {
            cb.and(root.get<Long>(Contract::type.name).`in`(typeIdList))
        } else {
            cb.and(root.get<Long>(Contract::type.name).`in`(ContractType.values().map { it.id }.toList()))
        }
        val condition = form.bool(ObjAttr.CONDITION)
        if (condition != null ) {
            val sectionJoin = root.join<Contract, ContractSection>(Contract::sectionList.name, JoinType.INNER)
            sectionJoin.on(cb.equal(sectionJoin.get<Long>(ContractSection::number.name), 0))
            predicateList += if (condition) {
                cb.isNull(sectionJoin.get<LocalDateTime>(ObjAttr.ARCHIVE_DATE))
            } else {
                cb.isNotNull(sectionJoin.get<LocalDateTime>(ObjAttr.ARCHIVE_DATE))
            }
        }
        val conditionalName = form.string(ObjAttr.CONDITIONAL_NAME)
        if (!conditionalName.isNullOrBlank()) {
            val sectionJoin = root.join<Contract, ContractSection>(Contract::sectionList.name, JoinType.INNER)
            sectionJoin.on(cb.equal(sectionJoin.get<Long>(ContractSection::number.name), ZERO_INT))
            val lotGroupJoin = sectionJoin.join<ContractSection, LotGroup>(ContractSection::lotGroupList.name, JoinType.INNER)
            predicateList += cb.like(lotGroupJoin.get<Product>(LotGroup::product.name).get(Product::conditionalName.name), "%$conditionalName%")
        }
        return predicateList.toTypedArray()
    }

    // TODO данный метод просто перевел из java в kotlin (с использованием DynamicObject не переделывал)
    override fun queryDataByFilterForm(
        tableDataIn: TabrIn,
        form: LetterContractFilterForm
    ): TabrResultQuery<Contract> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)
        select.where(*predicateListByFilterForm(form, root, cb))
        val sectionJoin = root.join<Contract, ContractSection>(Contract::sectionList.name, JoinType.INNER)
        sectionJoin.on(cb.equal(sectionJoin.get<Int>(ContractSection::number.name), 0))
        tableDataIn.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                Contract::fullNumber.name -> {
                    prSort(sectionJoin.get<Int>(ContractSection::year.name))
                    prSort(root.get<Long>(Contract::number.name))
                }
                Company::shortName.name -> prSort(root.get<String>(Company::shortName.name))
                else -> prSort(root.get<Long>(Contract::id.name))
            }
            cq.orderBy(orderList)
        }
        val tq = em.createQuery(select)
        tq.firstResult = tableDataIn.start
        tq.maxResults = tableDataIn.size
        val resultList = tq.resultList
        return TabrResultQuery.instance(resultList)
    }

    private fun predicateListByFilterForm(
        form: LetterContractFilterForm,
        root: Root<Contract>,
        cb: CriteriaBuilder
    ): Array<Predicate> {
        val predicateList = mutableListOf<Predicate>()
        predicateList.add(cb.equal(root.get<Performer>(Contract::performer.name), Performer.OAOKORUND))

        val contractNumber = form.number
        if (contractNumber.isNotBlank()) {
            predicateList.add(cb.equal(root.get<Long>(Contract::number.name).`as`(String::class.java), contractNumber))
        }

        val customer = form.customer
        if (customer.isNotBlank()) {
            val customerJoin = root.join<Contract, Company>(Contract::customer.name, JoinType.LEFT)
            predicateList.add(cb.like(customerJoin[Company::name.name], "%$customer%"))
        }

        return predicateList.toTypedArray()
    }
}