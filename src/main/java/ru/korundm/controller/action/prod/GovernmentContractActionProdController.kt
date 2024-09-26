package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.BaseService
import ru.korundm.dao.GovernmentContractService
import ru.korundm.entity.GovernmentContract
import ru.korundm.entity.ServiceType
import ru.korundm.exception.AlertUIException
import ru.korundm.form.GovernmentContractListEditForm
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@ActionController([RequestPath.Action.Prod.GOVERNMENT_CONTRACT])
class GovernmentContractActionProdController(
    private val jsonMapper: ObjectMapper,
    private val governmentContractService: GovernmentContractService,
    private val baseService: BaseService
) {

    // Загрузка госконтрактов
    @GetMapping("/list/load")
    fun listLoad(request: HttpServletRequest, filterData: String): TabrOut<*> {
        data class Item(
            val id: Long?,
            val identifier: String, // идентификатор
            val date: LocalDate?, // дата заключения
            val comment: String? //комментарий
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, governmentContractService.findTableData(input, form)) { gov ->
            gov.identifier?.let {
                Item(
                    gov.id,
                    it,
                    gov.date,
                    gov.comment
                )
            }
        }
    }

    // Загрузка информации о госконтраке
    @GetMapping("/list/account-info/load")
    fun listStructureLoad(
        govContractId: Long,
        request: HttpServletRequest
    ): List<*> {
        data class Item(
            val govContractId: Long?,
            val separateAccount: String?, // ОБС - отдельный банковский счет
            val bankInfo: String, // наименование и местоположение банка
            val sectionInfo: String, // номер договора/дополнения
            val company: String?, // заказчик
            val status: Boolean // статус ОБС
        )
        val govContract = governmentContractService.read(govContractId)
        return govContract.accountList.map { account ->
            val bank = account.bank
            Item (
                account.governmentContract?.id,
                account.account,
                bank?.name + ", " + bank?.location,
                account.sectionList.map {
                    if (it.number == 0) {
                        it.contract?.fullNumber
                    } else {
                        it.fullNumber
                    }
                }.toList().joinToString(", "),
                account.sectionList[0].contract?.customer?.name,
                account.sectionList.any { it.archiveDate == null }
            )
        }.toList()
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(session: HttpSession, form: GovernmentContractListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formId = form.id
            lateinit var govContract: GovernmentContract
            baseService.exec {
                govContract = formId?.let { governmentContractService.read(it) ?: throw AlertUIException("Госконтракт не найден") } ?: GovernmentContract()
                govContract.apply {
                    identifier = form.identifier
                    date = form.date
                    comment = form.comment.nullIfBlank()
                    governmentContractService.save(this)
                }
            }
            if (formId == null) response.putAttribute(ServiceType::id.name, govContract.id)
        }
        return response
    }

    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        governmentContractService.read(id)?.apply {
            if (this.accountList.isNotEmpty()) throw AlertUIException("Для данного госконтракта существуют ОБС. Удаление невозможно.")
            governmentContractService.delete(this)
        }
    }
}