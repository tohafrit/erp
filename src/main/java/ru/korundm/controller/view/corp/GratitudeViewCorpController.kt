package ru.korundm.controller.view.corp

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.CompanyService
import ru.korundm.dao.FileStorageService
import ru.korundm.dao.GratitudeService
import ru.korundm.enumeration.CompanyTypeEnum
import ru.korundm.form.GratitudeEditForm
import ru.korundm.helper.FileStorageType

@ViewController([RequestPath.View.Corp.GRATITUDE])
class GratitudeViewCorpController(
    private val gratitudeService: GratitudeService,
    private val companyService: CompanyService,
    private val fileStorageService: FileStorageService
) {

    @GetMapping("/list")
    fun list() = "corp/include/gratitude/list"

    // Редактирование благодарности
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val form = GratitudeEditForm()
        id?.let {
            val gratitude = gratitudeService.read(it)
            form.id = it
            form.companyId = gratitude.company!!.id
            form.date = gratitude.date
            form.fileStorage = fileStorageService.readOneSingular(gratitude, FileStorageType.GratitudeParamFile)
        }
        model["form"] = form
        model["companyList"] = companyService.getAllByType(CompanyTypeEnum.CUSTOMERS)
        return "corp/include/gratitude/list/edit"
    }
}