package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.DocumentLabelService
import ru.korundm.dao.UserService
import ru.korundm.dto.DropdownOption
import ru.korundm.exception.AlertUIException
import ru.korundm.util.KtCommonUtil.getUser
import javax.servlet.http.HttpSession
import ru.korundm.form.DocumentLabelListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.DOCUMENT_LABEL])
class DocumentLabelViewProdController(
    private val documentLabelService: DocumentLabelService,
    private val userService: UserService
) {

    @GetMapping("/list")
    fun list() = "prod/include/document-label/list"

    @GetMapping("/list/filter")
    fun listFilter() = "prod/include/document-label/list/filter"

    @GetMapping("/list/edit")
    fun listEdit(
        model: ModelMap,
        id: Long?,
        session: HttpSession,
    ): String {
        val user = session.getUser()
        val documentLabel = id?.let { documentLabelService.read(it) ?: throw AlertUIException("Метка шаблона не найдена") }
        val userList = userService.activeAll.map { DropdownOption(it.id, it.userOfficialName) }
        model.addAttribute("form", ListEditForm(id).apply {
            label = documentLabel?.label ?: ""
            employeePosition = documentLabel?.employeePosition ?: ""
            comment = documentLabel?.comment ?: ""
            employee = documentLabel?.user ?: user
        })
        model.addAttribute("userList", userList)
        return "prod/include/document-label/list/edit"
    }
}