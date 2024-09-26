package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.BaseService
import ru.korundm.dao.LaunchProductService
import ru.korundm.dao.LaunchService
import ru.korundm.entity.Launch
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.KtCommonUtil.getUser
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import ru.korundm.form.LaunchListAdditionalEditForm as ListAdditionalEditForm
import ru.korundm.form.LaunchListEditForm as ListEditForm

@ActionController([RequestPath.Action.Prod.LAUNCH])
class LaunchActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val launchService: LaunchService,
    private val launchProductService: LaunchProductService
) {

    @GetMapping("/list/load")
    fun listLoad(request: HttpServletRequest, filterData: String): TabrOut<*> {
        data class Item(
            val id: Long?,
            val numberInYear: String, // номер в году
            val approvalDate: LocalDate?, // дата утверждения
            val approvedBy: String?, // утвердивший пользователь
            val comment: String?, // комментарий
            val canEdit: Boolean, // возможность редактирования
            val canDelete: Boolean, // возможность удаления
            val canApprove: Boolean, // возможность утверждения
            val canUnApprove: Boolean // возможность снятия утверждения
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        val lastLaunch = launchService.findLastLaunch()
        return TabrOut.instance(input, launchService.findTableData(input, form)) { l -> Item(
            l.id,
            l.numberInYear,
            l.approvalDate,
            l.approvedBy?.userOfficialName,
            l.comment,
            l.approvalDate == null,
            l.approvalDate == null,
            l.approvalDate == null,
            l == lastLaunch && l.approvalDate != null
        ) }
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formId = form.id
            lateinit var launch: Launch
            baseService.exec {
                verifyCanAddLaunch(formId, launchService)
                launch = formId?.let { launchService.read(it) ?: throw AlertUIException("Запуск не найден") } ?: Launch()
                if (launch.approvalDate != null) throw AlertUIException("Редактирование утвержденного запуска невозможно")
                launch.apply {
                    version = form.version
                    year = formId?.let { year } ?: LocalDate.now().year
                    number = formId?.let { number } ?: launchService.getLastNumber(year)
                    comment = form.comment.nullIfBlank()
                    launchService.save(this)
                }
            }
            if (formId == null) response.putAttribute(Launch::id.name, launch.id)
        }
        return response
    }

    @PostMapping("/list/approve")
    fun listApprove(
        session: HttpSession,
        id: Long,
        toApprove: Boolean
    ) = baseService.exec {
        val user = session.getUser()
        val launch = launchService.read(id) ?: throw AlertUIException("Запуск не найден")
        if (toApprove && launch.approvalDate != null) throw AlertUIException("Запуск был утвержден")
        if (!toApprove && launch != launchService.findLastLaunch()) throw AlertUIException("Снять утверждение возможно только с последнего запуска")
        if (!toApprove && launch.approvalDate == null) throw AlertUIException("Утверждение было снято с запуска")
        launch.approvalDate = if (toApprove) LocalDate.now() else null
        launch.approvedBy = if (toApprove) user else null
        launchService.save(launch)
    }

    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        launchService.read(id)?.apply {
            if (approvalDate != null) throw AlertUIException("Невозможно удалить утвержденный запуск")
            if (launchService.hasLaunches(id)) throw AlertUIException("Невозможно завершить удаление - запуск имеет зависимые запуски")
            launchService.delete(this)
        }
    }

    @GetMapping("/list/additional/load")
    fun listAdditionalLoad(
        request: HttpServletRequest,
        launchId: Long,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?,
            val numberInYear: String, // номер в году
            val approvalDate: LocalDate?, // дата утверждения
            val approvedBy: String?, // утвердивший пользователь
            val comment: String?, // комментарий
            val canEdit: Boolean, // возможность редактирования
            val canDelete: Boolean, // возможность удаления
            val canApprove: Boolean, // возможность утверждения
            val canUnApprove: Boolean // возможность снятия утверждения
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        val parentLaunch = launchService.read(launchId) ?: throw AlertUIException("Запуск-родитель не найден")
        val nextParentLaunch = launchService.findNextLaunch(parentLaunch.year, parentLaunch.number)
        val lastLaunch = launchService.findLastLaunch(parentLaunch.id)
        return TabrOut.instance(input, launchService.findTableData(input, form, launchId)) { l -> Item(
            l.id,
            l.numberInYear,
            l.approvalDate,
            l.approvedBy?.userOfficialName,
            l.comment,
            l.approvalDate == null,
            l.approvalDate == null,
            l.approvalDate == null,
            l == lastLaunch && nextParentLaunch?.approvalDate == null && l.approvalDate != null
        ) }
    }

    @PostMapping("/list/additional/edit/save")
    fun listAdditionalEditSave(form: ListAdditionalEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formId = form.id
            lateinit var launch: Launch
            baseService.exec {
                var parentId = form.launchId
                if (formId != null) {
                    launch = launchService.read(formId) ?: throw AlertUIException("Запуск не найден")
                    parentId = launch.launch?.id
                } else launch = Launch()
                val parentLaunch = parentId?.let { launchService.read(it) } ?: throw AlertUIException("Запуск-родитель не найден")
                verifyCanAddLaunch(formId, parentLaunch, launchService)
                if (launch.approvalDate != null) throw AlertUIException("Редактирование утвержденного запуска невозможно")
                launch.apply {
                    version = form.version
                    this.launch = parentLaunch
                    year = parentLaunch.year
                    number = formId?.let { number } ?: launchService.getLastNumber(year, parentLaunch.id)
                    comment = form.comment.nullIfBlank()
                    launchService.save(this)
                }
            }
            if (formId == null) response.putAttribute(Launch::id.name, launch.id)
        }
        return response
    }

    @PostMapping("/list/additional/approve")
    fun listAdditionalApprove(
        session: HttpSession,
        id: Long,
        toApprove: Boolean
    ) = baseService.exec {
        val user = session.getUser()
        // Установка утверждения
        val launch = launchService.read(id) ?: throw AlertUIException("Запуск не найден")
        if (toApprove && launch.approvalDate != null) throw AlertUIException("Запуск был утвержден")
        // Снятие утверждения
        val pLaunch = launch.launch ?: throw AlertUIException("Запуск-родитель не найден")
        val nextLaunch = launchService.findNextLaunch(pLaunch.year, pLaunch.number)
        if (!toApprove && (launch != launchService.findLastLaunch(pLaunch.id) || nextLaunch?.let { it.approvalDate != null } == true)) {
            throw AlertUIException("Снятие утверждения невозможно")
        }
        if (!toApprove && launch.approvalDate == null) throw AlertUIException("Утверждение было снято с запуска")
        //
        launch.approvalDate = if (toApprove) LocalDate.now() else null
        launch.approvedBy = if (toApprove) user else null
        launchService.save(launch)
    }

    @DeleteMapping("/list/additional/delete/{id}")
    fun listAdditionalDelete(@PathVariable id: Long) = baseService.exec {
        launchService.read(id)?.apply {
            if (approvalDate != null) throw AlertUIException("Невозможно удалить утвержденный запуск")
            launchService.delete(this)
        }
    }

    @GetMapping("/detail/load")
    fun detailLoad(
        request: HttpServletRequest,
        launchId: Long,
        filterData: String
    ): TabrOut<*> {
        val input = TabrIn(request)
        return TabrOut.instance(input, launchProductService.findDetailTableData(input, launchId, jsonMapper.readDynamic(filterData)))
    }

    companion object {

        fun verifyCanAddLaunch(id: Long?, launchService: LaunchService) {
            val lastLaunch = launchService.findLastLaunch()
            if (id == null && lastLaunch != null && lastLaunch.approvalDate == null) throw AlertUIException("Невозможно добавить запуск не утвердив предыдущий")
        }

        fun verifyCanAddLaunch(id: Long?, parentLaunch: Launch, launchService: LaunchService) {
            if (id == null) {
                val nextParentLaunch = launchService.findNextLaunch(parentLaunch.year, parentLaunch.number)
                val lastLaunch = launchService.findLastLaunch(parentLaunch.id)
                if (parentLaunch.approvalDate == null || nextParentLaunch?.let { it.approvalDate != null } == true || lastLaunch?.let { it.approvalDate == null } == true) {
                    throw AlertUIException("Невозможно добавить запуск")
                }
            }
        }
    }
}