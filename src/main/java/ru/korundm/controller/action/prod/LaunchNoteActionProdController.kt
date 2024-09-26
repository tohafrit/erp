package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.BaseService
import ru.korundm.dao.LaunchNoteProductService
import ru.korundm.dao.LaunchNoteService
import ru.korundm.entity.LaunchNote
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.RowCountable
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.KtCommonUtil.getUser
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.numberInYear
import ru.korundm.util.KtCommonUtil.readDynamic
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import ru.korundm.form.LaunchNoteListEditForm as ListEditForm

@ActionController([RequestPath.Action.Prod.LAUNCH_NOTE])
class LaunchNoteActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val launchNoteService: LaunchNoteService,
    private val launchNoteProductService: LaunchNoteProductService
) {

    @GetMapping("/list/load")
    fun listLoad(request: HttpServletRequest, filterData: String): TabrOut<*> {
        data class Item(
            val id: Long?,
            val numberInYear: String, // номер в году
            val createDate: LocalDate?, // дата создания
            val createdBy: String?, // создан
            val agreementDate: LocalDate?, // дата согласования
            val agreedBy: String?, // согласован
            val comment: String?, // комментарий
            val canDelete: Boolean, // возможность удаления
            val canAgreement: Boolean, // возможность согласования
            val canUnAgreement: Boolean // возможность снятия согласования
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, launchNoteService.findListTableData(input, form)) { note -> Item(
            note.id,
            note.numberInYear,
            note.createDate,
            note.createdBy?.userOfficialName,
            note.agreementDate,
            note.agreedBy?.userOfficialName,
            note.comment,
            note.agreementDate == null,
            note.agreementDate == null,
            note.agreementDate != null
        ) }
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(session: HttpSession, form: ListEditForm): ValidatorResponse {
        val user = session.getUser()
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formId = form.id
            lateinit var note: LaunchNote
            baseService.exec {
                note = formId?.let { launchNoteService.read(it) ?: throw AlertUIException("Служебная записка не найдена") } ?: LaunchNote()
                val nowDate = LocalDate.now()
                note.apply {
                    version = form.version
                    year = formId?.let { year } ?: nowDate.year
                    number = formId?.let { number } ?: launchNoteService.getLastNumber(year)
                    createDate = formId?.let { createDate } ?: nowDate
                    createdBy = formId?.let { createdBy } ?: user
                    comment = form.comment.nullIfBlank()
                    launchNoteService.save(this)
                }
            }
            if (formId == null) response.putAttribute(LaunchNote::id.name, note.id)
        }
        return response
    }

    @PostMapping("/list/agreement")
    fun listAgreement(
        session: HttpSession,
        id: Long,
        toAgreement: Boolean
    ) = baseService.exec {
        val user = session.getUser()
        val note = launchNoteService.read(id) ?: throw AlertUIException("Служебная записка не найдена")
        if (toAgreement && note.agreementDate != null) throw AlertUIException("Служебная записка была согласована")
        if (toAgreement && !launchNoteProductService.existsByNote(note)) throw AlertUIException("Невозможно согласовать записку без изделий")
        if (!toAgreement && note.agreementDate == null) throw AlertUIException("Согласование было снято со служебной записки")
        note.agreementDate = if (toAgreement) LocalDate.now() else null
        note.agreedBy = if (toAgreement) user else null
        launchNoteService.save(note)
    }

    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        launchNoteService.read(id)?.apply {
            if (agreementDate != null) throw AlertUIException("Невозможно удалить согласованную записку")
            if (launchNoteProductService.existsByNote(this)) throw AlertUIException("Невозможно завершить удаление - записка содержит изделия")
            launchNoteService.delete(this)
        }
    }

    @GetMapping("/list/product/load")
    fun listProductLoad(
        request: HttpServletRequest,
        filterData: String,
        noteId: Long
    ): TabrOut<*> {
        data class Item(
            var id: Long? = null,
            var year: Int = 0,
            var number: Int = 0,
            var parentNumber: Int? = null,
            var productName: String = "",
            var contractAmount: Int = 0,
            var rfContractAmount: Int = 0,
            var rfAssembledAmount: Int = 0,
            var ufrfContractAmount: Int = 0,
            var ufrfAssembledAmount: Int = 0,
            var ufrfContractInOtherProductAmount: Int = 0,
            var rowCount: Long = 0,
            //
            var numberInYear: String = ""
        ) : RowCountable { override fun rowCount() = rowCount }
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        val tableResultQuery = launchNoteProductService.findNoteProductTableData(input, noteId, form, Item::class)
        return TabrOut.instance(input, tableResultQuery) {
            it.numberInYear = numberInYear(it.year, it.number, it.parentNumber)
            it
        }
    }
}