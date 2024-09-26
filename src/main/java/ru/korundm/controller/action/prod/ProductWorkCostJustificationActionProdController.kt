package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.dao.*
import ru.korundm.entity.ProductWorkCost
import ru.korundm.entity.ProductWorkCostJustification
import ru.korundm.entity.WorkType
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.*
import ru.korundm.util.FileStorageUtil.extractSingular
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import ru.korundm.util.KtCommonUtil.safetyReadListValue
import ru.korundm.util.KtCommonUtil.safetyReadMutableListValue
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest
import ru.korundm.form.ProductWorkCostJustificationListEditForm as ListEditForm

@ActionController([RequestPath.Action.Prod.PRODUCT_WORK_COST_JUSTIFICATION])
class ProductWorkCostJustificationActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val productWorkCostJustificationService: ProductWorkCostJustificationService,
    private val productWorkCostService: ProductWorkCostService,
    private val workTypeService: WorkTypeService,
    private val fileStorageService: FileStorageService,
    private val productDeciphermentAttrValService: ProductDeciphermentAttrValService
) {

    // Загрузка списка обоснований
    @GetMapping("/list/load")
    fun listLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            var id: Long? = null,
            var name: String = "", // наименование
            var approvalDate: LocalDate = LocalDate.MIN, // дата утверждения
            var createDate: LocalDate = LocalDate.MIN, // дата создания
            var comment: String? = "", // комментарий
            var rowCount: Long = 0, // номер строки для пангинации
            //
            var fileHash: String? = null // файл
        ) : RowCountable { override fun rowCount() = rowCount }
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        val tableResultQuery = productWorkCostJustificationService.findTableData(input, form, Item::class)
        val entityList = tableResultQuery.data.map { ProductWorkCostJustification(it.id) }
        val fileList = fileStorageService.readAny(entityList, FileStorageType.ProductWorkCostJustificationFile)
        return TabrOut.instance(input, tableResultQuery) {
            it.fileHash = fileList.extractSingular(entityList.find { e -> e.id == it.id }!!, FileStorageType.ProductWorkCostJustificationFile)?.urlHash
            it
        }
    }

    // Загрузка списка работ для окна редактирования
    @GetMapping("/list/edit/add-work/load")
    fun listEditAddWorkLoad(workTypeData: String): List<*> {
        data class Item(
            var id: Long? = null,
            var name: String = ""
        )
        return workTypeService.getAllByIdNotIn(jsonMapper.safetyReadListValue(workTypeData, Long::class)).map { Item(it.id, it.name) }
    }

    // Удаление обоснования
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        if (productDeciphermentAttrValService.existsByProductWorkCostJustificationId(id)) throw AlertUIException("Невозможно удалить обоснование, т.к оно используется в расчетах")
        productWorkCostService.deleteByJustificationId(id)
        productWorkCostJustificationService.deleteById(id)
    }

    // Сохранение обоснования
    @PostMapping("/list/edit/save")
    fun listEditSave(form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        val formId = form.id
        val workCostFormList = jsonMapper.safetyReadMutableListValue(form.workCost, ListEditForm.WorkCost::class)
        baseService.exec {
            val workTypeIdList = workTypeService.getAllIdList()
            workCostFormList.removeIf { workTypeIdList.contains(it.id).not() }
            if (workCostFormList.isEmpty()) response.putStringError(ListEditForm::workCost.name, "необходимо указать хотя бы одну действующую работу")
            if (workCostFormList.any { it.cost == .0 }) response.putStringError(ListEditForm::workCost.name, "стоимость работ не может содержать нулевые значения")
            if (response.isValid) {
                val justification = formId?.let { productWorkCostJustificationService.read(it) ?: throw AlertUIException("Обоснование не найдено") } ?: ProductWorkCostJustification()
                val isEditable = productDeciphermentAttrValService.existsByProductWorkCostJustificationId(justification.id).not()
                justification.apply {
                    version = form.version
                    name = form.name
                    approvalDate = form.approvalDate!!
                    createDate = if (formId == null) LocalDate.now() else createDate
                    comment = form.comment.nullIfBlank()
                    productWorkCostJustificationService.save(this)
                }
                // Файл
                if (form.fileStorage?.id == null) fileStorageService.saveEntityFile(justification, FileStorageType.ProductWorkCostJustificationFile, form.file)
                // Привязка работ
                if (isEditable) {
                    val productWorkCostList = productWorkCostService.getAllByJustificationId(justification.id).toMutableList()
                    val workCostToSaveList = mutableListOf<ProductWorkCost>()
                    workCostFormList.forEach { wcf ->
                        var pwc = productWorkCostList.find { pwc -> wcf.id == pwc.workType?.id }
                        if (pwc == null) {
                            pwc = ProductWorkCost()
                            pwc.justification = justification
                            pwc.workType = WorkType(wcf.id)
                        }
                        pwc.cost = wcf.cost
                        workCostToSaveList += pwc
                    }
                    productWorkCostList.removeIf { pwc -> workCostToSaveList.any { pwcs -> pwc.workType?.id == pwcs.workType?.id } }
                    productWorkCostService.deleteAll(productWorkCostList)
                    productWorkCostService.saveAll(workCostToSaveList)
                }
                if (formId == null) response.putAttribute(ObjAttr.ID, justification.id)
            }
        }
        return response
    }
}