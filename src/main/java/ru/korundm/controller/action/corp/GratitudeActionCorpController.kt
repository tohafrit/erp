package ru.korundm.controller.action.corp

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.FileStorageService
import ru.korundm.dao.GratitudeService
import ru.korundm.entity.Company
import ru.korundm.entity.Gratitude
import ru.korundm.form.GratitudeEditForm
import ru.korundm.helper.ValidatorResponse
import ru.korundm.helper.FileStorageType
import ru.korundm.util.FileStorageUtil.extractSingular
import java.time.LocalDate

@ActionController([RequestPath.Action.Corp.GRATITUDE])
class GratitudeActionCorpController(
    private val gratitudeService: GratitudeService,
    private val fileStorageService: FileStorageService
) {

    // Загрузка благодарностей
    @GetMapping("/list/load")
    fun listLoad(): List<*> {
        data class Item(
            val id: Long?, // идентификатор
            val company: String?, // наименование компании
            val date: LocalDate?, // дата
            var fileUrlHash: String? // хэш файла
        )
        val gratitudeList = gratitudeService.all
        val fileList = fileStorageService.readAny(gratitudeList)
        return gratitudeList.map { gratitude -> Item(
            gratitude.id,
            gratitude.company?.name,
            gratitude.date,
            fileList.extractSingular(gratitude, FileStorageType.GratitudeParamFile)?.urlHash
        ) }.toList()
    }

    // Сохранение благодарности
    @PostMapping("/list/edit/save")
    fun listEditSave(form: GratitudeEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            (form.id?.let { gratitudeService.read(it) } ?: Gratitude()).apply{
                company = Company(form.companyId)
                date = form.date
                gratitudeService.save(this)
                if (form.file?.isEmpty == false) {
                    fileStorageService.saveEntityFile(this, FileStorageType.GratitudeParamFile, form.file)
                }
            }
        }
        return response
    }

    // Удаление благодарности
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) {
        val gratitude = gratitudeService.read(id)
        val fileStorage = fileStorageService.readOneSingular(gratitude, FileStorageType.GratitudeParamFile)
        fileStorage?.let { fileStorageService.delete(it) }
        gratitudeService.deleteById(id)
    }
}