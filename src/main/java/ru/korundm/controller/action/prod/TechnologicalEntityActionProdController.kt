package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.*
import ru.korundm.annotation.ActionController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg.RANGE_LENGTH
import ru.korundm.constant.ValidatorMsg.REQUIRED
import ru.korundm.dao.*
import ru.korundm.entity.*
import ru.korundm.enumeration.ServiceSymbolType
import ru.korundm.enumeration.ServiceSymbolType.*
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.*
import ru.korundm.helper.TabrOut.Companion.instance
import ru.korundm.util.KtCommonUtil.readDynamic
import ru.korundm.util.KtCommonUtil.safetyReadListValue
import java.time.LocalDate
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@ActionController([RequestPath.Action.Prod.TECHNOLOGICAL_ENTITY])
class TechnologicalEntityActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val technologicalEntityService: TechnologicalEntityService,
    private val technologicalEntityTypeService: TechnologicalEntityTypeService,
    private val technologicalEntityReconciliationService: TechnologicalEntityReconciliationService,
    private val technologicalEntityApplicabilityService: TechnologicalEntityApplicabilityService,
    private val productService: ProductService,
    private val serviceSymbolService: ServiceSymbolService,
    private val productionAreaService: ProductionAreaService,
    private val laborProtectionInstructionService: LaborProtectionInstructionService,
    private val equipmentService: EquipmentService,
    private val technologicalToolService: TechnologicalToolService,
    private val operationMaterialService: OperationMaterialService,
    private val functionalityService: FunctionalityService,
    private val workTypeService: WorkTypeService,
    private val productLetterService: ProductLetterService,
    private val technologicalEntityAgreedService: TechnologicalEntityAgreedService
) {

    // Загрузка сущностей
    @GetMapping("/list/load")
    fun listLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        data class Item(
            val id: Long?,
            val entityNumber: String,
            val setNumber: String,
            val description: String,
            val notificationNumber: String?,
            val termChangeOn: LocalDate? = null,
            val approved: Boolean = false,
            val approvedTooltip: String? = null,
            val designed: Boolean = false,
            val designedTooltip: String? = null,
            val checked: Boolean = false,
            val checkedTooltip: String? = null,
            val metrologist: Boolean = false,
            val metrologistTooltip: String? = null,
            val normocontroller: Boolean = false,
            val normocontrollerTooltip: String? = null,
            val military: Boolean = false,
            val militaryTooltip: String? = null,
            val technologicalChief: Boolean = false,
            val technologicalChiefTooltip: String? = null
        )
        return instance(input, technologicalEntityService.findTableData(input, form)) { entity ->
            val lastNotification = entity.lastNotification
            val reconciliation =  entity.technologicalEntityReconciliation
            val termChangeOn = lastNotification?.termChangeOn ?: reconciliation?.designedOn?.toLocalDate()
            val applicability = entity.technologicalEntityApplicabilityList.joinToString(", ") {
                it.product?.techSpecName ?: ""
            }
            val description = "${entity.entityType?.shortName} на $applicability от ${termChangeOn?.format(DATE_FORMATTER)}"
            val tooltip = { userFullName: String?, date: LocalDateTime? -> "${userFullName ?: ""} ${date.let{DATE_FORMATTER.format(it)} ?: ""}" }
            Item(
                entity.id,
                entity.entityNumber,
                entity.setNumber,
                description,
                lastNotification?.docNumber,
                lastNotification?.termChangeOn,
                reconciliation?.approved ?: false,
                tooltip(reconciliation?.approvedBy?.userOfficialName, reconciliation?.approvedOn),
                reconciliation?.designed ?: false,
                tooltip(reconciliation?.designedBy?.userOfficialName, reconciliation?.designedOn),
                reconciliation?.checked ?: false,
                tooltip(reconciliation?.checkedBy?.userOfficialName, reconciliation?.checkedOn),
                reconciliation?.metrologist ?: false,
                tooltip(reconciliation?.metrologistBy?.userOfficialName, reconciliation?.metrologistOn),
                reconciliation?.normocontroller ?: false,
                tooltip(reconciliation?.normocontrollerBy?.userOfficialName, reconciliation?.normocontrollerOn),
                reconciliation?.military ?: false,
                tooltip(reconciliation?.militaryBy?.userOfficialName, reconciliation?.militaryOn),
                reconciliation?.technologicalChief ?: false,
                tooltip(reconciliation?.technologicalChiefBy?.userOfficialName, reconciliation?.technologicalChiefOn),
            )
        }
    }

    // Загрузка изделий для добавления применяемости
    @GetMapping("/list/edit/add-product/load")
    fun listEditAddProductLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?,
            val conditionalName: String?, // условное наименование
            val decimalNumber: String?, // технические условия изделия
            val typeName: String? // краткая техническая характеристика
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return instance(input, productService.findTableData(input, form)) { Item(
            it.id,
            it.conditionalName,
            it.decimalNumber,
            it.type?.name
        ) }
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(@RequestPart form: DynamicObject): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)
        val entityNumber = form.stringNotNull(ObjAttr.ENTITY_NUMBER).trim()
        if (entityNumber.isEmpty()) errors.putError(ObjAttr.ENTITY_NUMBER, RANGE_LENGTH, 1, 128)
        val setNumber = form.stringNotNull(ObjAttr.SET_NUMBER).trim()
        if (setNumber.isEmpty()) errors.putError(ObjAttr.SET_NUMBER, RANGE_LENGTH, 1, 128)

        val entityType = form.long(ObjAttr.ENTITY_TYPE_ID)?.let { technologicalEntityTypeService.read(it) } ?: throw AlertUIException("Тип документации не указан")
        val productApplicabilityIdList = form.listLong(ObjAttr.PRODUCT_APPLICABILITY_ID_LIST)
        if (!entityType.multi && productApplicabilityIdList.isEmpty()) errors.putError(ObjAttr.PRODUCT_APPLICABILITY_ID_LIST, REQUIRED)

        if (response.isValid) baseService.exec {
            val formId = form.long(ObjAttr.ID)
            val technologicalEntity = formId?.let { technologicalEntityService.read(formId) } ?: TechnologicalEntity()

            technologicalEntity.apply {
                this.entityNumber = entityNumber
                this.setNumber = setNumber
                this.entityType = entityType
                productLetter = form.long(ObjAttr.PRODUCT_LETTER_ID)?.let { productLetterService.read(it) }
            }
            technologicalEntityService.save(technologicalEntity)

            technologicalEntityApplicabilityService.deleteAllByTechnologicalEntity(technologicalEntity)
            val technologicalEntityApplicabilityList = productApplicabilityIdList.map {
                TechnologicalEntityApplicability().apply {
                    this.technologicalEntity = technologicalEntity
                    product = Product(it)
                }
            }
            technologicalEntityApplicabilityService.saveAll(technologicalEntityApplicabilityList)

            val reconciliation = technologicalEntity.technologicalEntityReconciliation ?: TechnologicalEntityReconciliation()
            val now = LocalDateTime.now()
            reconciliation.apply {
                this.technologicalEntity = technologicalEntity

                approved = form.bool(ObjAttr.APPROVED) ?: false
                approvedBy = User(form.longNotNull(ObjAttr.APPROVED_BY))
                approvedOn = now

                designed = form.bool(ObjAttr.DESIGNED) ?: false
                designedBy = User(form.longNotNull(ObjAttr.DESIGNED_BY))
                designedOn = now

                checked = form.bool(ObjAttr.CHECKED) ?: false
                checkedBy = User(form.longNotNull(ObjAttr.CHECKED_BY))
                checkedOn = now

                metrologist = form.bool(ObjAttr.METROLOGIST) ?: false
                metrologistBy = User(form.longNotNull(ObjAttr.METROLOGIST_BY))
                metrologistOn = now

                military = form.bool(ObjAttr.MILITARY) ?: false
                militaryBy = User(form.longNotNull(ObjAttr.MILITARY_BY))
                militaryOn = now

                technologicalChief = form.bool(ObjAttr.TECHNOLOGICAL_CHIEF) ?: false
                technologicalChiefBy = User(form.longNotNull(ObjAttr.TECHNOLOGICAL_CHIEF_BY))
                technologicalChiefOn = now

                normocontroller = form.bool(ObjAttr.NORMOCONTROLLER) ?: false
                normocontrollerBy = User(form.longNotNull(ObjAttr.NORMOCONTROLLER_BY))
                normocontrollerOn = now
            }
            technologicalEntityReconciliationService.save(reconciliation)

            val agreedList = reconciliation.agreedList
            if (agreedList.isNotEmpty()) technologicalEntityAgreedService.deleteAll(agreedList)

            val agreedIdList = form.listLong(ObjAttr.AGREED_BY)
            val newAgreedList = if (agreedIdList.isNotEmpty()) agreedIdList.map {
                val agreed = TechnologicalEntityAgreed().apply {
                    technologicalEntityReconciliation = reconciliation
                    agreedBy = User(it)
                    agreedOn = now
                    agreed = form.bool("${ObjAttr.AGREED}-$it") ?: false
                }
                agreed
            } else emptyList()
            technologicalEntityAgreedService.saveAll(newAgreedList)

            if (formId == null) {
                response.putAttribute(ObjAttr.ID, technologicalEntity.id)
                response.putAttribute(ObjAttr.ENTITY_TYPE_ID, technologicalEntity.entityType?.id)
            }
        }
        return response
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = technologicalEntityService.deleteById(id)

    @GetMapping("/detail/child/load")
    fun detailChildLoad(
        parentId: Long
    ): List<*> {
        data class Item(
            val id: Long?,
            val symbol: String?,
            val equipment: String?,
            val content: String?,
            val mode: String?,
            val technologicalTool: String?,
            val operationMaterial: String?,
            val koid: Int?,
            val ksht: String?,
            val tpz: String?,
            val tsht: String?
        )
        return functionalityService.read(parentId).childList.map { child ->
            Item(
                child.id,
                child.serviceSymbol?.name,
                child.bFunctionality?.equipmentList?.joinToString(separator = "; ") { equipment -> equipment.name },
                child.oFunctionality?.content,
                child.rFunctionality?.mode,
                child.tFunctionality?.technologicalToolList?.joinToString(separator = "; ") { tool -> tool.name },
                child.mFunctionality?.operationMaterialList?.joinToString(separator = "; ") { material -> material.name },
                child.bFunctionality?.koid,
                child.bFunctionality?.ksht,
                child.bFunctionality?.tpz,
                child.bFunctionality?.tsht,
            )
        }
    }

    @GetMapping("/detail/labour/load")
    fun detailLabourLoad(
        entityId: Long
    ): List<*> {
        data class LabourItem(
            val number: String?,
            val workType: String?,
            val labourValue: Double?
        )
        return functionalityService.getAllByCode(entityId, A.code).reversed().map { func ->
            val bFunc = func.childList.firstOrNull { it.serviceSymbol?.code == B.code }
            bFunc?.let { b ->
                val koid = b.bFunctionality?.koid
                val ksht = b.bFunctionality?.ksht?.toDouble() ?: Double.MIN_VALUE
                val tpz = b.bFunctionality?.tpz?.toDouble() ?: Double.MIN_VALUE
                val tsht = b.bFunctionality?.tsht?.toDouble() ?: Double.MIN_VALUE
                LabourItem(func.aFunctionality?.number, func.aFunctionality?.workType?.name, koid?.let { (tpz/it + ksht*tsht) } ?: Double.MIN_VALUE)
            }
        }
    }

    @GetMapping("/detail/load")
    fun detailLoad(
        entityId: Long
    ): List<*> {
        data class Item(
            val id: Long?,
            val symbol: String?,
            val areaCode: String?,
            val number: String?,
            val workType: String?,
            val description: String?,
            val laborProtectionInstruction: String?
        )
        return functionalityService.getAllByEntity(entityId).map { func -> Item(
            func.id,
            func.serviceSymbol?.name,
            func.aFunctionality?.productionAreaList?.joinToString(separator = "; ") { area -> area.formatCode },
            func.aFunctionality?.number,
            "${func.aFunctionality?.workType?.name}${func.aFunctionality?.nameComment?.let { if (it.isNotEmpty()) " ($it)" else "" }}",
            func.aFunctionality?.description,
            func.aFunctionality?.laborProtectionInstructionList?.joinToString(separator = "; ") { labor -> "ИОТ №${labor.number}" }
        ) }
    }

    @PostMapping("/detail/edit/save")
    fun detailEditSave(@RequestPart form: DynamicObject): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)
        val serviceSymbolType = form.string(ObjAttr.SYMBOL)?.let { ServiceSymbolType.getByCode(it) ?: throw AlertUIException("Не указан функциональный переход") }
        when(serviceSymbolType) {
            A -> {
                val productionAreaIdList = form.listLong(ObjAttr.PRODUCTION_AREA_ID_LIST)
                if (productionAreaIdList.isEmpty()) errors.putError(ObjAttr.PRODUCTION_AREA_ID_LIST, REQUIRED)
                val number = form.string(ObjAttr.NUMBER)
                if (number.isNullOrEmpty()) errors.putError(ObjAttr.NUMBER, RANGE_LENGTH, 1, 64)
                val nameComment = form.string(ObjAttr.NAME_COMMENT)
                nameComment?.let { if (it.length > 512) errors.putError(ObjAttr.NAME_COMMENT, RANGE_LENGTH, 1, 512) }
                val description = form.string(ObjAttr.DESCRIPTION)
                description?.let { if (it.length > 1024) errors.putError(ObjAttr.DESCRIPTION, RANGE_LENGTH, 1, 1024) }
                val workTypeId = form.long(ObjAttr.WORK_TYPE_ID)
                if (workTypeId == null) errors.putError(ObjAttr.WORK_TYPE_ID, REQUIRED)
                val laborIdList = form.listLong(ObjAttr.LABOR_ID_LIST)
                if (laborIdList.isEmpty()) errors.putError(ObjAttr.LABOR_ID_LIST, REQUIRED)
            }
            B -> {
                val equipmentIdList = form.listLong(ObjAttr.EQUIPMENT_ID_LIST)
                if (equipmentIdList.isEmpty()) errors.putError(ObjAttr.EQUIPMENT_ID_LIST, REQUIRED)
                val koid = form.int(ObjAttr.KOID)
                if (koid == null || koid == 0) errors.putError(ObjAttr.KOID, REQUIRED)
                val ksht = form.string(ObjAttr.KSHT) ?: ""
                if (ksht.isEmpty()) errors.putError(ObjAttr.KSHT, REQUIRED)
                val tpz = form.string(ObjAttr.TPZ) ?: ""
                if (tpz.isEmpty()) errors.putError(ObjAttr.TPZ, REQUIRED)
                val tsht = form.string(ObjAttr.TSHT) ?: ""
                if (tsht.isEmpty()) errors.putError(ObjAttr.TSHT, REQUIRED)
            }
            O -> {
                val contentList = form.listString(ObjAttr.CONTENT)
                if (contentList.isEmpty()) errors.putError(ObjAttr.CONTENT, REQUIRED)
            }
            T -> {
                val toolIdList = form.listLong(ObjAttr.TOOL_ID_LIST)
                if (toolIdList.isEmpty()) errors.putError(ObjAttr.TOOL_ID_LIST, REQUIRED)
            }
            M -> {
                val materialIdList = form.listLong(ObjAttr.MATERIAL_ID_LIST)
                if (materialIdList.isEmpty()) errors.putError(ObjAttr.MATERIAL_ID_LIST, REQUIRED)
            }
            else -> Unit
        }
        if (response.isValid) baseService.exec {
            val formId = form.long(ObjAttr.ID)
            val functionality = formId?.let { functionalityService.read(it) } ?: Functionality()
            val parent = form.long(ObjAttr.PARENT_ID)?.let { functionalityService.read(it) }
            val entity = form.long(ObjAttr.ENTITY_ID)?.let { technologicalEntityService.read(it) } ?: throw AlertUIException("Технологическая документация не определена")
            functionality.apply {
                technologicalEntity = entity
                this.parent = parent
                serviceSymbol = serviceSymbolService.getByCode(serviceSymbolType?.name)
                sort = formId?.let { this.sort } ?: functionalityService.getAllByEntity(entity).size
            }
            val functionalityList = mutableListOf<Functionality>()
            when(serviceSymbolType) {
                A -> {
                    val productionAreaIdList = form.listLong(ObjAttr.PRODUCTION_AREA_ID_LIST)
                    val workType = form.long(ObjAttr.WORK_TYPE_ID)?.let { workTypeService.read(it) } ?: throw AlertUIException("Тип работы не найден")
                    val laborIdList = form.listLong(ObjAttr.LABOR_ID_LIST)
                    val aFunctionality = functionality.aFunctionality ?: AFunctionality()
                    aFunctionality.apply {
                        productionAreaList = productionAreaService.getAllById(productionAreaIdList)
                        this.workType = workType
                        number = form.string(ObjAttr.NUMBER)
                        nameComment = form.string(ObjAttr.NAME_COMMENT)
                        description = form.string(ObjAttr.DESCRIPTION)

                        laborProtectionInstructionList = laborProtectionInstructionService.getAllById(laborIdList)
                    }
                    functionality.aFunctionality = aFunctionality
                    functionalityList += functionality
                }
                B -> {
                    val bFunctionality = functionality.bFunctionality ?: BFunctionality()
                    bFunctionality.apply {
                        equipmentList = equipmentService.getAllById(form.listLong(ObjAttr.EQUIPMENT_ID_LIST))
                        koid = form.int(ObjAttr.KOID)
                        ksht = form.string(ObjAttr.KSHT)
                        tpz = form.string(ObjAttr.TPZ)
                        tsht = form.string(ObjAttr.TSHT)
                    }
                    functionality.bFunctionality = bFunctionality
                    functionalityList += functionality
                }
                T -> {
                    val tFunctionality = functionality.tFunctionality ?: TFunctionality()
                    tFunctionality.technologicalToolList = technologicalToolService.getAllById(form.listLong(ObjAttr.TOOL_ID_LIST))
                    functionality.tFunctionality = tFunctionality
                    functionalityList += functionality
                }
                M -> {
                    val mFunctionality = functionality.mFunctionality ?: MFunctionality()
                    mFunctionality.operationMaterialList = operationMaterialService.getAllById(form.listLong(ObjAttr.MATERIAL_ID_LIST))
                    functionality.mFunctionality = mFunctionality
                    functionalityList += functionality
                }
                O -> {
                    functionalityList += form.listString(ObjAttr.CONTENT).mapIndexed { idx, value ->
                        val copyFunctionality = functionality.copy()
                        val oFunctionality = OFunctionality()
                        oFunctionality.apply {
                            content = value
                            contentSort = idx
                        }
                        copyFunctionality.oFunctionality = oFunctionality
                        copyFunctionality
                    }
                    functionalityService.delete(functionality)
                }
                R -> {
                    functionalityService.removeAllByParams(entity, parent, serviceSymbolType.name)
                    functionalityList += form.listString(ObjAttr.MODE).map {
                        val copyFunctionality = functionality.copy()
                        val rFunctionality = RFunctionality()
                        rFunctionality.mode = it
                        copyFunctionality.rFunctionality = rFunctionality
                        copyFunctionality
                    }
                }
            }
            functionalityService.saveAll(functionalityList)
        }
        return response
    }

    @DeleteMapping("/detail/delete/{id}")
    fun detailDelete(@PathVariable id: Long) = functionalityService.deleteById(id)

    @GetMapping("/detail/edit/add-area/load")
    fun listEditAddAreaLoad(idList: String): List<*> {
        data class Item(
            val id: Long?,
            val code: String?,
            val name: String?
        )
        return productionAreaService.findTableData(jsonMapper.safetyReadListValue(idList, Long::class)).map { Item(it.id, it.formatCode, it.name) }
    }

    @GetMapping("/detail/edit/add-labor/load")
    fun listEditAddLaborLoad(idList: String): List<*> {
        data class Item(
            val id: Long?,
            val name: String?
        )
        return laborProtectionInstructionService.findTableData(jsonMapper.safetyReadListValue(idList, Long::class)).map { Item(it.id, "ИОТ №${it.number} ${it.name}") }
    }

    @GetMapping("/detail/edit/add-equipment/load")
    fun listEditAddEquipmentLoad(idList: String): List<*> {
        data class Item(
            val id: Long?,
            val name: String?,
            val model: String?
        )
        return equipmentService.findTableData(jsonMapper.safetyReadListValue(idList, Long::class)).map { Item(it.id, it.name, it.model) }
    }

    @GetMapping("/detail/edit/add-tool/load")
    fun listEditAddToolLoad(idList: String): List<*> {
        data class Item(
            val id: Long?,
            val sign: String?,
            val name: String?
        )
        return technologicalToolService.findTableData(jsonMapper.safetyReadListValue(idList, Long::class)).map { Item(it.id, it.sign, it.name) }
    }

    @GetMapping("/detail/edit/add-material/load")
    fun listEditAddMaterialLoad(idList: String, parentId: Long): List<*> {
        val func = functionalityService.read(parentId)
        data class Item(
            val id: Long?,
            val name: String?
        )
        return operationMaterialService.findTableData(jsonMapper.safetyReadListValue(idList, Long::class), func.aFunctionality?.workType?.id).map { Item(it.id, it.name) }
    }

    @PostMapping("/detail/update-sort")
    fun detailUpdateSort(idList: String) {
        val functionalityList = jsonMapper.safetyReadListValue(idList, Long::class)
            .mapIndexed { index, id ->
                val functionality = functionalityService.read(id)
                functionality.sort = index
                functionality
            }
        functionalityService.saveAll(functionalityList)
    }
}