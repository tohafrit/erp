package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.AccountService
import ru.korundm.dao.BankService
import ru.korundm.dao.BaseService
import ru.korundm.dao.CompanyService
import ru.korundm.entity.Account
import ru.korundm.entity.ServiceType
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import ru.korundm.form.AccountListEditForm as tListEditForm

@ActionController([RequestPath.Action.Prod.ACCOUNT])
class AccountActionProdController(
    private val jsonMapper: ObjectMapper,
    private val accountService: AccountService,
    private val companyService: CompanyService,
    private val bankService: BankService,
    private val baseService: BaseService
) {

    // Загрузка расчетных счетов
    @GetMapping("/list/load")
    fun listLoad(request: HttpServletRequest, filterData: String): TabrOut<*> {
        data class Item(
            val id: Long?,
            val account: String?, // номер расчетного счета
            val bankName: String?, // банк
            val customer: String?, // заказчик
            val identifier: String?, // идентификатор госконтракта
            val contractNumber: String, // договор
            val status: Boolean?, // дата договора или дополнительного соглашения
            val comment: String? //комментарий
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, accountService.findTableData(input, form)) { account ->
            val sectionSeparateAccountList = account.sectionList.filter { it.separateAccount != null }
            Item(
                account.id,
                account.account,
                account.bank?.name,
                account.company?.name,
                account.governmentContract?.identifier,
                sectionSeparateAccountList.joinToString(", ") { it.fullNumber },
                if (sectionSeparateAccountList.isNotEmpty()) sectionSeparateAccountList.all { it.archiveDate != null } else null,
                account.note
            )
        }
    }

    // Загрузка заказчиков
    @GetMapping("/list/edit/customer/load")
    fun listEditCustomerLoad(
        request: HttpServletRequest,
        model: ModelMap,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?,
            val name: String, // название заказчика
            val address: String? // адрес
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, companyService.findTableData(null, input, form)) { company ->
            Item(
                company.id,
                company.name,
                company.juridicalAddress
            )
        }
    }

    // Загрузка выбранного заказчика при добавлении или редактировании расчетного счета
    @GetMapping("/list/edit/customer-selected/load")
    fun listEditCustomerSelectedLoad(
        customerId: Long?
    ): List<*>? {
        data class Item(
            val id: Long?,
            val name: String, // название заказчика
            val address: String? // адрес
        )
        val itemList = mutableListOf<Item>()
        (customerId?.let { companyService.read(it) })?.apply { itemList += Item(id, name, juridicalAddress) }
        return itemList
    }

    // Загрузка банков
    @GetMapping("/list/edit/bank/load")
    fun listEditBankLoad(
        request: HttpServletRequest,
        model: ModelMap,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?,
            val name: String?, // название банка
            val location: String? // местонахождение
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, bankService.findTableData(input, form)) { bank ->
            Item(
                bank.id,
                bank.name,
                bank.location
            )
        }
    }

    // Загрузка выбранного банка при добавлении или редактировании расчетного счета
    @GetMapping("/list/edit/bank-selected/load")
    fun listEditBankSelectedLoad(
        bankId: Long?
    ): List<*>? {
        data class Item(
            val id: Long?,
            val name: String?, // название банка
            val location: String? // местонахождение
        )
        val itemList = mutableListOf<Item>()
        (bankId?.let { bankService.read(it) })?.apply { itemList += Item(id, name, location) }
        return itemList
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(session: HttpSession, form: tListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formId = form.id
            lateinit var account: Account
            baseService.exec {
                account = formId?.let { accountService.read(it) ?: throw AlertUIException("Расчетный счет не найден") } ?: Account()
                account.apply {
                    note = form.comment
                    this.account = form.accountNumber
                    bank = form.bankId?.let { bankService.read(it) }
                    company = form.customerId?.let { companyService.read(it) }
                    note = form.comment.nullIfBlank()
                    accountService.save(this)
                }
            }
            if (formId == null) response.putAttribute(ServiceType::id.name, account.id)
        }
        return response
    }

    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) {
        val account = accountService.read(id)
        val sectionSeparateAccountList = account.sectionList.filter { it.separateAccount != null }
        val contractNumber = sectionSeparateAccountList.joinToString(", ") { it.fullNumber }
        if (sectionSeparateAccountList.isNotEmpty()) throw AlertUIException("Данный ОБС связан с договором №$contractNumber. Удаление невозможно")
        // TODO данное условие работает только для созданных в erp invoice. В эко не было связи account-a c invoice
        // TODO временно вместо isNotEmpty поставил isEmpty. Удалить расчетный счет пока возможно только через базу данных
        if (account.invoiceList.isEmpty()) throw AlertUIException("Данный расчетный счет связан со счетами на оплату. Удаление невозможно")
        accountService.deleteById(id)
    }
}