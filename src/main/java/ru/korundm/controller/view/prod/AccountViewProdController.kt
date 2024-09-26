package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.AccountService
import ru.korundm.exception.AlertUIException
import ru.korundm.form.AccountListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.ACCOUNT])
class AccountViewProdController(
    private val accountService: AccountService
) {

    @GetMapping("/list")
    fun list(model: ModelMap) = "prod/include/account/list"

    @GetMapping("/list/filter")
    fun listFilter() = "prod/include/account/list/filter"

    // Редактирование/добавление расчетного счета
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val account = id?.let { accountService.read(it) ?: throw AlertUIException("Расчетный счет не найден") }
        model.addAttribute("form", ListEditForm(id).apply {
            accountNumber = account?.account
            bankId = account?.bank?.id
            customerId = account?.company?.id
            comment = account?.note ?: ""
        })
        model.addAttribute("isEditCustomer", account?.invoiceList?.isNotEmpty() ?: false)
        return "prod/include/account/list/edit"
    }

    @GetMapping("/list/edit/customer")
    fun listEditCustomer() = "prod/include/account/list/edit/customer"

    @GetMapping("/list/edit/customer/filter")
    fun listEditCustomerFilter(model: ModelMap) = "prod/include/account/list/edit/customer/filter"

    @GetMapping("/list/edit/bank")
    fun listEditBank() = "prod/include/account/list/edit/bank"

    @GetMapping("/list/edit/bank/filter")
    fun listEditBankFilter(model: ModelMap) = "prod/include/account/list/edit/bank/filter"
}