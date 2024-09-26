package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.GovernmentContractService
import ru.korundm.exception.AlertUIException
import java.time.LocalDate
import ru.korundm.form.GovernmentContractListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.GOVERNMENT_CONTRACT])
class GovernmentContractViewProdController(
    private val governmentContractService: GovernmentContractService
) {

    @GetMapping("/list")
    fun list(model: ModelMap) = "prod/include/government-contract/list"

    @GetMapping("/list/filter")
    fun listFilter(model: ModelMap): String {
        return "prod/include/government-contract/list/filter"
    }

    // Информация о госконтракте
    @GetMapping("/list/account-info")
    fun listStructure(
        model: ModelMap,
        id: Long
    ): String {
        val govContract = governmentContractService.read(id)
        model.addAttribute("id", id)
        model.addAttribute("identifier", govContract.identifier)
        return "prod/include/government-contract/list/accountInfo"
    }

    // Редактирование/добавление госконтракта
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val govContract = id?.let { governmentContractService.read(it) ?: throw AlertUIException("Госконтракт не найден") }
        if (govContract != null) {
            if (govContract.accountList.isNotEmpty()) throw AlertUIException("Для данного госконтракта существуют ОБС. Редактирование невозможно.")
        }
        model.addAttribute("form", ListEditForm(id).apply {
            identifier = govContract?.identifier
            date = govContract?.date ?: LocalDate.now()
            comment = govContract?.comment ?: ""
        })
        return "prod/include/government-contract/list/edit"
    }
}