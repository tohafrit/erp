package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.io.FilenameUtils.getExtension
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.korundm.annotation.ActionController
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.dao.*
import ru.korundm.entity.*
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.AttachmentMediaType.XLS
import ru.korundm.helper.AttachmentMediaType.XLSX
import ru.korundm.helper.RowCountable
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.KtCommonUtil.getUser
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import ru.korundm.util.KtCommonUtil.safetyReadListValue
import ru.korundm.util.KtCommonUtil.userFullName
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import ru.korundm.dto.prod.ProductLabourIntensityExcelImportDto as ExcelImportDto
import ru.korundm.dto.prod.ProductLabourIntensityExcelImportProductDto as ExcelImportProduct
import ru.korundm.form.ProductLabourIntensityDetailEditForm as DetailEditForm
import ru.korundm.form.ProductLabourIntensityListEditForm as ListEditForm

@ActionController([RequestPath.Action.Prod.PRODUCT_LABOUR_INTENSITY])
class ProductLabourIntensityActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val productLabourIntensityService: ProductLabourIntensityService,
    private val productLabourIntensityEntryService: ProductLabourIntensityEntryService,
    private val productLabourIntensityOperationService: ProductLabourIntensityOperationService,
    private val productService: ProductService,
    private val workTypeService: WorkTypeService,
    private val productDeciphermentAttrValService: ProductDeciphermentAttrValService
) {

    @GetMapping("/list/load")
    fun listLoad(request: HttpServletRequest, filterData: String): TabrOut<*> {
        data class Item(
            var id: Long? = null,
            var name: String = "",
            var comment: String? = null,
            var createDate: LocalDate = LocalDate.MIN,
            var lastName: String? = null,
            var firstName: String? = null,
            var middleName: String? = null,
            var approvedCount: Int = 0,
            var totalCount: Int = 0,
            var rowCount: Long = 0,
            var createdBy: String? = null,
            var approved: String? = null
        ) : RowCountable { override fun rowCount() = rowCount }
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, productLabourIntensityService.findTableData(input, form, Item::class)) {
            it.createdBy = userFullName(it.lastName, it.firstName, it.middleName)
            it.approved = "${it.approvedCount}/${it.totalCount}"
            it
        }
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(session: HttpSession, form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formId = form.id
            lateinit var labour: ProductLabourIntensity
            baseService.exec {
                labour = formId?.let { productLabourIntensityService.read(it) ?: throw AlertUIException("Расчет турдоемкости не найден") } ?: ProductLabourIntensity()
                labour.apply {
                    version = form.version
                    name = form.name
                    comment = form.comment.nullIfBlank()
                    createDate = if (id == null) form.createDate!! else createDate
                    createdBy = if (id == null) session.getUser() else createdBy
                    productLabourIntensityService.save(this)
                }
            }
            if (formId == null) response.putAttribute(ObjAttr.ID, labour.id)
        }
        return response
    }

    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        if (productLabourIntensityEntryService.isApprovedByJustificationId(id)) throw AlertUIException("Список не может быть удален, поскольку содержит утвержденные трудоемкости")
        if (productDeciphermentAttrValService.existsByProductLabourIntensityId(id)) throw AlertUIException("Список не может быть удален, поскольку используется в расчете цен")
        productLabourIntensityOperationService.deleteAllByJustificationId(id)
        productLabourIntensityEntryService.deleteAllByJustificationId(id)
        productLabourIntensityService.deleteById(id)
    }

    @GetMapping("/detail/load")
    fun detailLoad(request: HttpServletRequest, filterData: String, labourIntensityId: Long): TabrOut<*> {
        data class Item(
            var id: Long? = null,
            var productName: String? = null,
            var decimalNumber: String? = null,
            var totalLabourIntensity: Double = .0,
            var createDate: LocalDate = LocalDate.MIN,
            var creatorLastName: String? = null,
            var creatorFirstName: String? = null,
            var creatorMiddleName: String? = null,
            var approvalDate: LocalDate? = null,
            var approverLastName: String? = null,
            var approverFirstName: String? = null,
            var approverMiddleName: String? = null,
            var comment: String? = null,
            var rowCount: Long = 0,
            var createdBy: String? = null,
            var approvedBy: String? = null,

            var canApprove: Boolean = false
        ) : RowCountable { override fun rowCount() = rowCount }
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, productLabourIntensityEntryService.findTableData(input, form, labourIntensityId, Item::class)) {
            it.createdBy = userFullName(it.creatorLastName, it.creatorFirstName, it.creatorMiddleName)
            it.approvedBy = userFullName(it.approverLastName, it.approverFirstName, it.approverMiddleName)
            it.canApprove = it.approvalDate == null
            it
        }
    }

    @PostMapping("/detail/edit/save")
    fun detailEditSave(session: HttpSession, form: DetailEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            baseService.exec {
                val labour = form.id?.let { productLabourIntensityEntryService.read(it) ?: throw AlertUIException("Трудоемкость не найдена") }
                labour?.apply {
                    version = form.version
                    comment = form.comment.nullIfBlank()
                    productLabourIntensityEntryService.save(this)
                }
            }
        }
        return response
    }

    @DeleteMapping("/detail/delete/{id}")
    fun detailDelete(@PathVariable id: Long) = baseService.exec {
        productLabourIntensityEntryService.read(id).let {
            if (it.approvalDate != null) throw AlertUIException("Трудоемкость утверждена. Удаление невозможно")
            productLabourIntensityOperationService.deleteAllByEntryId(it.id)
            productLabourIntensityEntryService.delete(it)
        }
    }

    @PostMapping("/detail/approve")
    fun detailApprove(session: HttpSession, id: Long, toApprove: Boolean) = baseService.exec {
        val labour = productLabourIntensityEntryService.read(id) ?: throw AlertUIException("Трудоемкость не найдена")
        if (toApprove && labour.approvalDate != null) throw AlertUIException("Трудоемкость была утверждена")
        if (!toApprove && labour.approvalDate == null) throw AlertUIException("Трудоемкость была снята с утверждения")
        if (!toApprove && productDeciphermentAttrValService.existsByProductIdAndLabourIntensityId(labour.product?.id, labour.labourIntensity?.id)) {
            throw AlertUIException("Невозможно снять утверждение, поскольку трудоемкость используется в расчетах цены изделия")
        }
        labour.approvalDate = if (toApprove) LocalDate.now() else null
        labour.approvedBy = if (toApprove) session.getUser() else null
    }

    @PostMapping("/detail/add/import")
    fun detailAddImport(
        @RequestParam file: MultipartFile,
        @RequestParam labourIntensityId: Long
    ): ExcelImportDto {
        val wb = when (getExtension(file.originalFilename ?: file.name)?.lowercase()) {
            XLS.extension -> HSSFWorkbook(file.inputStream)
            XLSX.extension -> XSSFWorkbook(file.inputStream)
            else -> throw AlertUIException("Некорректный формат файла")
        }
        val data = ExcelImportDto()
        val sheet: Sheet = wb.getSheetAt(0)

        var dataRowCount = 1 // счетчик строк в данных разбора
        var rowNumber = 0 // счетчик строк в документе разбора

        val notSystemOpNameList = mutableListOf<String>() // виды работ, которые отсутствуют в системе
        val existsProductIdList = mutableListOf<Long>() // разобранные изделия, должны быть уникальны

        // Глобальные обозначение текущего изделия в итерации
        var product: Product? = null // изделие
        var entry: ProductLabourIntensityEntry? = null // общая трудоемкость (одна на изделие)
        var productName = "" // наименование
        var productDecNumber = "" // децимальный номер
        var operationList = emptyList<ProductLabourIntensityOperation>() // набор значений трудоемкости в системе по каждому виду работ для изделия
        val existsOpIdList = mutableListOf<Long>() // разобранные виды работ по каждому изделию, должны быть уникальны

        // Функция сброса изделия итерации
        val clearProduct = {
            product = null
            entry = null
            productName = ""
            productDecNumber = ""
            operationList = emptyList()
            existsOpIdList.clear()
        }

        // Функция дополнения данных результатами изменения значений трудоемкости для тех строк, что были в системе, но не были в импортируемом документе
        val systemResultData = { operationList.forEach { if (!existsOpIdList.contains(it.operation?.id)) {
            data.resultList.add(ExcelImportDto.Result(
                dataRowCount++,
                product?.id,
                entry?.id,
                it.id,
                it.operation?.id,
                productName,
                productDecNumber,
                it.operation?.name ?: "",
                it.value,
                .0,
                .0
            ))
            existsOpIdList.add(it.operation?.id!!)
        } } }

        // Разбор лежит в транзакции, т.к периодически дергаем данные из БД
        baseService.exec {
            while (++rowNumber <= sheet.lastRowNum) {
                val row = sheet.getRow(rowNumber) ?: continue
                val cellProductName = row.getCell(1)?.let { if (it.cellType == CellType.STRING) it.stringCellValue.trim() else "" } ?: ""
                val cellProductDecNumber = row.getCell(2)?.let { if (it.cellType == CellType.STRING) it.stringCellValue.trim() else "" } ?: ""

                // Если мы начинаем разбор или наименование изделия было указано в строке, то необходимо определить это изделие
                if (rowNumber == 1 || cellProductName.isNotBlank()) {
                    systemResultData() // перед попыткой перехода на новое изделие в старом нужно добавить системные результирующие строки к данным

                    // Пытаемся определить изделие
                    clearProduct() // очищаем изделие итерации так как обрабатываем новое
                    val productList = productService.getByNameAndDecimalNumber(cellProductName, cellProductDecNumber)
                    when {
                        productList.isEmpty() -> data.errorList.add(ExcelImportDto.Error("Не удалось найти изделие по Условному наименованию '$cellProductName' и ТУ '$cellProductDecNumber' для строки ${rowNumber + 1}. Изделие было пропущено в разборе"))
                        productList.size == 1 -> {
                            val checkEntry = productLabourIntensityEntryService.getByLabourIntensityIdAndProductId(labourIntensityId, productList[0].id)
                            if (checkEntry?.approvalDate == null) {
                                // Нашли изделие - устанавливаем его в текущую итерацию разбора
                                product = productList[0]
                                entry = checkEntry
                                productName = cellProductName
                                productDecNumber = cellProductDecNumber
                                operationList = productLabourIntensityOperationService.getAllByLabourIntensityIdAndProductId(labourIntensityId, product?.id)
                                existsOpIdList.clear()
                            } else {
                                data.errorList.add(ExcelImportDto.Error("Изделие с Условным наименованием '$cellProductName' и ТУ '$cellProductDecNumber' в строке ${rowNumber + 1} было пропущено в разборе, поскольку было утверждено в системе"))
                            }
                        }
                        productList.size > 1 -> data.errorList.add(ExcelImportDto.Error("Для строки ${rowNumber + 1} в системе найдено более 1 изделия по Условному наименованию '$cellProductName' и ТУ '$cellProductDecNumber'. Изделие было пропущено в разборе"))
                    }

                    // Проверка на дубликаты изделий
                    product?.id?.let {
                        if (existsProductIdList.contains(it)) {
                            data.errorList.add(ExcelImportDto.Error("Изделие с Условным наименованием '$cellProductName' и ТУ '$cellProductDecNumber' в строке ${rowNumber + 1} было пропущено в разборе, поскольку разобрано ранее. Убедитесь, что документ содержит изделия, которые не повторяются"))
                            clearProduct()
                        } else existsProductIdList.add(it)
                    }
                }

                if (product == null) continue
                else {
                    val opName = row.getCell(3)?.let { if (it.cellType == CellType.STRING) it.stringCellValue.trim() else "" } ?: ""
                    if (opName.equals("итого", true)) continue
                    if (opName.isBlank()) {
                        data.errorList.add(ExcelImportDto.Error("Наименование вида работы в строке ${rowNumber + 1} содержит пустое или не строковое зачение и было пропущено при разборе"))
                        continue
                    }
                    val operation = workTypeService.getByName(opName)
                    // Проверка на наличие вида работы в системе
                    if (operation == null) {
                        if (!notSystemOpNameList.contains(opName)) {
                            notSystemOpNameList.add(opName)
                            data.errorList.add(ExcelImportDto.Error("Наименование вида работы '$opName' не было найдено в системе и было пропущено при разборе. Убедитесь, что вид работы был добавлен в справочник"))
                        }
                        continue
                    }
                    // Вид работ может быть уникален на каждую строку для каждого изделия
                    val opId = operation.id!!
                    if (existsOpIdList.contains(opId)) {
                        data.errorList.add(ExcelImportDto.Error("Вид работы в строке ${rowNumber + 1} был пропущен в разборе, поскольку разобран ранее. Убедитесь, что документ содержит неповторяющиеся виды работ по каждому изделию"))
                        continue
                    } else {
                        val opValue = BigDecimal(row.getCell(4)?.let { if (it.cellType == CellType.NUMERIC) it.numericCellValue else .0 } ?: .0).setScale(2, HALF_UP).toDouble()
                        if (opValue == .0) {
                            data.errorList.add(ExcelImportDto.Error("Вид работы в строке ${rowNumber + 1} имеет нулевое или нечисловое значение трудоемкости и был пропущен в разборе. Убедитесь, что документ содержит корректные значения трудоемкости"))
                            continue
                        }

                        existsOpIdList.add(opId)

                        val entryOperation = operationList.find { it.operation?.id == opId }
                        data.resultList.add(ExcelImportDto.Result(
                            dataRowCount++,
                            product?.id,
                            entry?.id,
                            entryOperation?.id,
                            opId,
                            productName,
                            productDecNumber,
                            opName,
                            entryOperation?.value ?: .0,
                            opValue,
                            opValue
                        ))
                    }
                }
            }
        }
        // Разобрали все данные, но к последнему изделию разбора нужно добавить данные по трудоемкости из системы
        systemResultData()
        return data
    }

    @PostMapping("/detail/add/apply")
    fun detailAddApply(
        session: HttpSession,
        @RequestParam data: String,
        @RequestParam labourIntensityId: Long
    ) {
        val user = session.getUser()
        val resultList = jsonMapper.safetyReadListValue(data, ExcelImportProduct::class)
        if (resultList.isEmpty()) throw AlertUIException("Список разбора пуст")
        baseService.exec {
            val productLabourIntensity = productLabourIntensityService.read(labourIntensityId) ?: throw AlertUIException("Связанный список трудоемкостей не найден")
            val entryDeleteList = mutableListOf<ProductLabourIntensityEntry>()
            val entryOperationDeleteList = mutableListOf<ProductLabourIntensityOperation>()
            val entrySaveList = mutableListOf<ProductLabourIntensityEntry>()
            val entryOperationSaveList = mutableListOf<ProductLabourIntensityOperation>()
            var i = 0
            main@ for (result in resultList) {
                val product = productService.read(result.id) ?: break

                var entry = productLabourIntensityEntryService.getByLabourIntensityIdAndProductId(labourIntensityId, product.id)
                val entryId = result.entryId
                // Если трудоемкость переданная пользователем отличается от того, что в БД то прерываем операцию
                if (
                    (entryId == null && entry != null)
                    || (entry == null && entryId != null)
                    || (entry != null && entryId != null && entryId != entry.id)
                ) break

                // Исходный список операций переданных пользователем должен быть идентичен тому, что есть в системе
                val entryOperationList = productLabourIntensityOperationService.getAllByLabourIntensityIdAndProductId(labourIntensityId, product.id)
                for (op in result.operationList.filterNot { it.id == null }) {
                    if (entryOperationList.none { op.id == it.id && op.oldVal == it.value}) break@main
                }
                for (entryOp in entryOperationList) {
                    if (result.operationList.none { entryOp.id == it.id && entryOp.value == it.oldVal}) break@main
                }

                // Если список операции содержит все нулевые значения, то такую трудоемкость нужно удалить, если она есть иначе не создавать
                val isOpListBlank = result.operationList.all { it.finalVal == .0 }
                if (isOpListBlank && entry != null) {
                    entryOperationDeleteList.addAll(entryOperationList)
                    entryDeleteList.add(entry)
                } else if (!isOpListBlank) {
                    if (entry == null) {
                        entry = ProductLabourIntensityEntry()
                        entry.labourIntensity = productLabourIntensity
                        entry.product = product
                        entry.createDate = LocalDate.now()
                        entry.createdBy = user
                        entrySaveList.add(entry)
                    }

                    // Трудоемкость по изделию не должна быть утверждена
                    if (entry.approvalDate != null) throw AlertUIException("Разбор содержит данные для изделий, трудоемкости которых были утверждены. Сохранение невозможно")

                    // Парсим операции для сохранения в БД
                    for (op in result.operationList) {
                        var entryOp = entryOperationList.find { op.id == it.id }
                        if (entryOp == null) {
                            if (op.finalVal != .0) {
                                entryOp = ProductLabourIntensityOperation()
                                entryOp.entry = entry
                                entryOp.operation = WorkType(op.opId)
                                entryOp.value = op.finalVal
                                entryOperationSaveList.add(entryOp)
                            }
                        } else {
                            if (op.finalVal == .0) entryOperationDeleteList.add(entryOp)
                            else entryOp.value = op.finalVal
                        }
                    }
                }
                i++
            }
            if (i != resultList.size) throw AlertUIException("Структура системных данных была изменена. Текущий разбор не действителен. Пожалуйста, повторите импорт файла еще раз")

            if (entrySaveList.isNotEmpty()) productLabourIntensityEntryService.saveAll(entrySaveList)
            if (entryOperationSaveList.isNotEmpty()) productLabourIntensityOperationService.saveAll(entryOperationSaveList)
            if (entryOperationDeleteList.isNotEmpty()) productLabourIntensityOperationService.deleteAllByIdList(entryOperationDeleteList.mapNotNull { it.id })
            if (entryDeleteList.isNotEmpty()) productLabourIntensityEntryService.deleteAllByIdList(entryDeleteList.mapNotNull { it.id })
        }
    }
}