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
import ru.korundm.entity.ClassificationGroup
import ru.korundm.entity.ProductSpecResearchJustification
import ru.korundm.entity.ProductSpecResearchPrice
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.FileStorageType.ProductSpecResearchJustificationFile
import ru.korundm.helper.RowCountable
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.FileStorageUtil.extractSingular
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import ru.korundm.util.KtCommonUtil.safetyReadListValue
import ru.korundm.util.KtCommonUtil.safetyReadMutableListValue
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest
import ru.korundm.form.ProductSpecResearchJustificationListEditForm as ListEditForm

@ActionController([RequestPath.Action.Prod.PRODUCT_SPEC_RESEARCH_JUSTIFICATION])
class ProductSpecResearchJustificationActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val productSpecResearchJustificationService: ProductSpecResearchJustificationService,
    private val productSpecResearchPriceService: ProductSpecResearchPriceService,
    private val classificationGroupService: ClassificationGroupService,
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
        val tableResultQuery = productSpecResearchJustificationService.findTableData(input, form, Item::class)
        val entityList = tableResultQuery.data.map { ProductSpecResearchJustification(it.id) }
        val fileList = fileStorageService.readAny(entityList, ProductSpecResearchJustificationFile)
        return TabrOut.instance(input, tableResultQuery) {
            it.fileHash = fileList.extractSingular(entityList.find { e -> e.id == it.id }!!, ProductSpecResearchJustificationFile)?.urlHash
            it
        }
    }

    // Загрузка списка групп для окна редактирования
    @GetMapping("/list/edit/add-group/load")
    fun listEditAddGroupLoad(classGroupData: String): List<*> {
        data class Item(
            var id: Long? = null,
            var number: Int = 0,
            var name: String = ""
        )
        return classificationGroupService.getAllByIdNotIn(jsonMapper.safetyReadListValue(classGroupData, Long::class)).map { Item(it.id, it.number, it.characteristic) }
    }

    // Удаление обоснования
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        if (productDeciphermentAttrValService.existsByProductSpecResearchJustificationId(id)) throw AlertUIException("Невозможно удалить обоснование, т.к оно используется в расчетах")
        productSpecResearchPriceService.deleteByJustificationId(id)
        productSpecResearchJustificationService.deleteById(id)
    }

    // Сохранение обоснования
    @PostMapping("/list/edit/save")
    fun listEditSave(form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        val formId = form.id
        val groupPriceFormList = jsonMapper.safetyReadMutableListValue(form.groupPrice, ListEditForm.GroupPrice::class)
        baseService.exec {
            val classGroupIdList = classificationGroupService.getAllIdList()
            groupPriceFormList.removeIf { classGroupIdList.contains(it.id).not() }
            if (groupPriceFormList.isEmpty()) response.putStringError(ListEditForm::groupPrice.name, "необходимо указать хотя бы одну действующую группу")
            if (groupPriceFormList.any { it.price == .0 }) response.putStringError(ListEditForm::groupPrice.name, "цена не может содержать нулевые значения")
            if (response.isValid) {
                val justification = formId?.let { productSpecResearchJustificationService.read(it) ?: throw AlertUIException("Обоснование не найдено") } ?: ProductSpecResearchJustification()
                val isEditable = productDeciphermentAttrValService.existsByProductSpecResearchJustificationId(justification.id).not()
                justification.apply {
                    version = form.version
                    name = form.name
                    approvalDate = form.approvalDate!!
                    createDate = if (formId == null) LocalDate.now() else createDate
                    comment = form.comment.nullIfBlank()
                    productSpecResearchJustificationService.save(this)
                }
                // Файл
                if (form.fileStorage?.id == null) fileStorageService.saveEntityFile(justification, ProductSpecResearchJustificationFile, form.file)
                // Привязка работ
                if (isEditable) {
                    val productGroupPriceList = productSpecResearchPriceService.getAllByJustificationId(justification.id)
                    val groupPriceToSaveList = groupPriceFormList.map { gpf ->
                        var psrp = productGroupPriceList.find { psrp -> gpf.id == psrp.group?.id }
                        if (psrp == null) {
                            psrp = ProductSpecResearchPrice()
                            psrp.justification = justification
                            psrp.group = ClassificationGroup(gpf.id)
                        }
                        psrp.price = gpf.price
                        psrp
                    }
                    productSpecResearchPriceService.saveAll(groupPriceToSaveList)
                    productSpecResearchPriceService.deleteAllByIdList(productGroupPriceList.filterNot { pwc -> groupPriceToSaveList.any { pwcs -> pwc.group == pwcs.group } }.mapNotNull { it.id })
                }
                if (formId == null) response.putAttribute(ObjAttr.ID, justification.id)
            }
        }
        return response
    }
}