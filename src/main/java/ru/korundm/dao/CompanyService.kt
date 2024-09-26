package ru.korundm.dao

import org.apache.commons.collections4.CollectionUtils
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.view.ContractConstant.INNER_CUSTOMERS
import ru.korundm.entity.Company
import ru.korundm.entity.CompanyType
import ru.korundm.enumeration.CompanyTypeEnum
import ru.korundm.enumeration.CustomerTypeEnum
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.helper.TabrSorter
import ru.korundm.repository.CompanyRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.*
import ru.korundm.form.CompanyListFilterForm as ListFilterForm

interface CompanyService : CommonService<Company> {

    fun getAllByType(vararg type: CompanyTypeEnum): List<Company>
    fun existsById(id: Long): Boolean
    fun getByTableDataIn(tableDataIn: TabrIn, form: ListFilterForm): List<Company>
    fun getCountByForm(form: ListFilterForm): Long
    fun getByName(name: String): Company?
    fun findTableData(customerTypeId: Long?, tableInput: TabrIn, form: DynamicObject): TabrResultQuery<Company>
    fun findDeciphermentForm1CustomerTableData(input: TabrIn, form: DynamicObject): TabrResultQuery<Company>
}

@Service
@Transactional
class CompanyServiceImpl(
    private val companyRepository: CompanyRepository
) : CompanyService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<Company> = companyRepository.findAll()

    override fun getAllById(idList: List<Long>): List<Company> = companyRepository.findAllById(idList)

    override fun save(obj: Company) = companyRepository.save(obj)

    override fun saveAll(objectList: List<Company>): List<Company> = companyRepository.saveAll(objectList)

    override fun read(id: Long): Company? = companyRepository.findById(id).orElse(null)

    override fun delete(obj: Company) = companyRepository.delete(obj)

    override fun deleteById(id: Long) = companyRepository.deleteById(id)

    override fun getAllByType(vararg type: CompanyTypeEnum) = companyRepository.findByCompanyTypeListTypeIn(type.toList())

    override fun existsById(id: Long) = companyRepository.existsById(id)

    override fun getByName(name: String) = companyRepository.findFirstByName(name)

    private fun getFormPredicateList(
        form: ListFilterForm,
        root: Root<Company>,
        cb: CriteriaBuilder
    ): List<Predicate> {
        val predicateList = mutableListOf<Predicate>()

        // Наименование
        val name = form.name
        if (name.isNotBlank()) predicateList += cb.like(root.get(Company::name.name), "%$name%")

        // ИНН
        val inn = form.inn
        if (inn.isNotBlank()) predicateList += cb.like(root.get(Company::inn.name), "%$inn%")

        // КПП
        val kpp = form.kpp
        if (kpp.isNotBlank()) predicateList += cb.like(root.get(Company::kpp.name), "%$kpp%")

        // Местоположение
        val location = form.location
        if (location.isNotBlank()) predicateList += cb.like(root.get(Company::location.name), "%$location%")

        // Почтовый адрес
        val mailAddress = form.mailAddress
        if (mailAddress.isNotBlank()) predicateList += cb.like(root.get(Company::mailAddress.name), "%$mailAddress%")

        // Тип компании
        val companyTypeJoin = root.join<Company, CompanyType>(Company::companyTypeList.name, JoinType.LEFT)
        predicateList += cb.equal(companyTypeJoin.get<Long>(CompanyType::type.name), form.typeId)

        return predicateList
    }

    private fun getFormOrder(root: Root<Company>, sorter: TabrSorter): List<Path<*>> {
        val orderExpressionList = mutableListOf<Path<*>>()
        when (sorter.field) {
            "name" -> orderExpressionList += root.get<String>(Company::name.name)
            "fullName" -> orderExpressionList += root.get<String>(Company::fullName.name)
            "chiefName" -> orderExpressionList += root.get<String>(Company::chiefName.name)
            "chiefPosition" -> orderExpressionList += root.get<String>(Company::chiefPosition.name)
            "phoneNumber" -> orderExpressionList += root.get<String>(Company::phoneNumber.name)
            "contactPerson" -> orderExpressionList += root.get<String>(Company::contactPerson.name)
            "location" -> orderExpressionList += root.get<String>(Company::location.name)
            "inn" -> orderExpressionList += root.get<String>(Company::inn.name)
            "kpp" -> orderExpressionList += root.get<String>(Company::kpp.name)
            "ogrn" -> orderExpressionList += root.get<String>(Company::ogrn.name)
            "inspectorName" -> orderExpressionList += root.get<String>(Company::inspectorName.name)
            "inspectorHead" -> orderExpressionList += root.get<String>(Company::inspectorHead.name)
            "note" -> orderExpressionList += root.get<String>(Company::note.name)
            else -> orderExpressionList += root.get<Long>(Company::id.name)
        }
        return orderExpressionList
    }

    override fun getByTableDataIn(tableDataIn: TabrIn, form: ListFilterForm): List<Company> {
        val cb = em.criteriaBuilder
        val criteria = cb.createQuery(Company::class.java)
        val root = criteria.from(Company::class.java)
        val predicateList = getFormPredicateList(form, root, cb)
        val select = criteria.select(root).where(*predicateList.toTypedArray())
        // Сортировка
        if (CollectionUtils.isNotEmpty(tableDataIn.sorters)) {
            val sorter = tableDataIn.sorters[0]
            val orderExpressionList = getFormOrder(root, sorter)
            val orderList =
                if (Sort.Direction.ASC == sorter.dir) orderExpressionList.map { cb.asc(it) }.toList() else orderExpressionList.map { cb.desc(it) }.toList()
            criteria.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select)
        typedQuery.firstResult = tableDataIn.start
        typedQuery.maxResults = tableDataIn.size
        return typedQuery.resultList
    }

    override fun getCountByForm(form: ListFilterForm): Long {
        val cb = em.criteriaBuilder
        val criteria = cb.createQuery(Long::class.java)
        val root = criteria.from(Company::class.java)
        val predicateList = getFormPredicateList(form, root, cb)
        criteria.select(cb.count(root.get<Long>(Company::id.name))).distinct(java.lang.Boolean.TRUE).where(*predicateList.toTypedArray())
        return em.createQuery(criteria).singleResult
    }

    override fun findTableData(
        customerTypeId: Long?,
        tableInput: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<Company> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(Company::class.java)
        val root = cq.from(Company::class.java)
        val select = cq.select(root)
        select.where(*filterPredicateList(customerTypeId, form, root, cb))
        tableInput.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                Company::shortName.name -> prSort(root.get<String>(Company::shortName.name))
                Company::juridicalAddress.name -> prSort(root.get<String>(Company::juridicalAddress.name))
                else -> prSort(root.get<Long>(Company::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select)
        typedQuery.firstResult = tableInput.start
        typedQuery.maxResults = tableInput.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun findDeciphermentForm1CustomerTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<Company> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(Company::class.java)
        val root = cq.from(Company::class.java)
        val select = cq.select(root)

        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.NAME -> prSort(root.get<String>(Company::name.name))
                ObjAttr.LOCATION -> prSort(root.get<String>(Company::location.name))
                else -> prSort(root.get<Long>(ObjAttr.ID))
            }
            cq.orderBy(orderList)
        }

        val predicateList = mutableListOf<Predicate>()
        val name = form.stringNotNull(ObjAttr.NAME)
        if (name.isNotBlank()) predicateList += cb.like(root.get(Company::name.name), "%$name%")
        val location = form.stringNotNull(ObjAttr.LOCATION)
        if (location.isNotBlank()) predicateList += cb.like(root.get(Company::location.name), "%$location%")

        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    private fun filterPredicateList(
        customerTypeId: Long?,
        form: DynamicObject,
        root: Root<Company>,
        cb: CriteriaBuilder
    ): Array<Predicate> {
        val predicateList = mutableListOf<Predicate>()
        if (customerTypeId == CustomerTypeEnum.INTERNAL.id) {
            val typeListJoin = root.join<Company, CompanyType>(Company::companyTypeList.name, JoinType.LEFT)
            predicateList += typeListJoin.get<Long>(CompanyType::type.name).`in`(INNER_CUSTOMERS)
        }
        form.string(ObjAttr.NAME)?.let { predicateList += cb.like(root.get(Company::name.name), "%$it%") }
        form.string(ObjAttr.ADDRESS)?.let { predicateList += cb.like(root.get(Company::juridicalAddress.name), "%$it%") }
        return predicateList.toTypedArray()
    }
}