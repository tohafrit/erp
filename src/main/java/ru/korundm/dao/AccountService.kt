package ru.korundm.dao

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.constant.ObjAttr
import ru.korundm.entity.Account
import ru.korundm.entity.Bank
import ru.korundm.entity.Company
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery
import ru.korundm.repository.AccountRepository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

interface AccountService : CommonService<Account> {

    fun getByAccount(account: String): Account?
    fun findTableData(input: TabrIn, form: DynamicObject): TabrResultQuery<Account>
    fun findTableDataByCompany(input: TabrIn, companyId: Long?): TabrResultQuery<Account>
    fun getAllByCompanyId(id: Long?): List<Account>
    fun existsByCompanyIdAndAccId(companyId: Long?, id: Long?): Boolean
}

@Service
@Transactional
class AccountServiceImpl(
    private val repository: AccountRepository
) : AccountService {

    private val cl = Account::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<Account> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<Account> = repository.findAllById(idList)

    override fun save(obj: Account) = repository.save(obj)

    override fun saveAll(objectList: List<Account>): List<Account> = repository.saveAll(objectList)

    override fun read(id: Long): Account? = repository.findById(id).orElse(null)

    override fun delete(obj: Account) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun getByAccount(account: String) = repository.findFirstByAccount(account)

    override fun findTableData(
        input: TabrIn,
        form: DynamicObject
    ): TabrResultQuery<Account> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        val predicateList = mutableListOf<Predicate>()
        form.string(ObjAttr.ACCOUNT)?.let { predicateList += cb.like(root.get(Account::account.name), "%$it%") }
        form.string(ObjAttr.BANK_NAME)?.let { predicateList += cb.like(root.get<Bank>(Account::bank.name).get(Bank::name.name), "%$it%") }
        form.string(ObjAttr.CUSTOMER)?.let { predicateList += cb.like(root.get<Company>(Account::company.name).get(Company::name.name), "%$it%") }
        form.string(ObjAttr.COMMENT)?.let { predicateList += cb.like(root.get(Account::note.name), "%$it%") }

        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            when (it.field) {
                ObjAttr.ACCOUNT_NUMBER -> prSort(root.get<String>(Account::account.name))
                ObjAttr.BANK_NAME -> prSort(root.get<Bank>(Account::bank.name).get<String>(Bank::name.name))
                ObjAttr.COMMENT -> prSort(root.get<LocalDate>(Account::note.name))
                else -> prSort(root.get<Long>(Account::id.name))
            }
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun findTableDataByCompany(input: TabrIn, companyId: Long?): TabrResultQuery<Account> {
        em.clear()
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(cl)
        val root = cq.from(cl)
        val select = cq.select(root)

        val predicateList = mutableListOf<Predicate>()
        companyId?.let { predicateList += cb.equal(root.get<Company>(Account::company.name), it) }

        input.sorter?.let {
            val orderList = mutableListOf<Order>()
            val prSort = { path: Path<*> -> orderList += if (Sort.Direction.ASC == it.dir) cb.asc(path) else cb.desc(path) }
            prSort(root.get<Long>(Account::id.name))
            cq.orderBy(orderList)
        }
        val typedQuery = em.createQuery(select.where(*predicateList.toTypedArray()))
        typedQuery.firstResult = input.start
        typedQuery.maxResults = input.size
        return TabrResultQuery.instance(typedQuery.resultList)
    }

    override fun getAllByCompanyId(id: Long?) = if (id == null) emptyList() else repository.findAllByCompanyId(id)

    override fun existsByCompanyIdAndAccId(companyId: Long?, id: Long?) = if (companyId == null || id == null) false else repository.existsByCompanyIdAndId(companyId, id)
}