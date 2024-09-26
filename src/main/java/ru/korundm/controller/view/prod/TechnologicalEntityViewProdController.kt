package ru.korundm.controller.view.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.*
import ru.korundm.dto.DropdownOption
import ru.korundm.entity.Functionality
import ru.korundm.enumeration.ServiceSymbolType.*
import ru.korundm.exception.AlertUIException
import ru.korundm.util.CommonUtil

@ViewController([RequestPath.View.Prod.TECHNOLOGICAL_ENTITY])
class TechnologicalEntityViewController(
    private val technologicalEntityService: TechnologicalEntityService,
    private val technologicalEntityTypeService: TechnologicalEntityTypeService,
    private val userService: UserService,
    private val jsonMapper: ObjectMapper,
    private val workTypeService: WorkTypeService,
    private val functionalityService: FunctionalityService,
    private val productLetterService: ProductLetterService
) {

    @GetMapping("/list")
    fun list() = "prod/include/technological-entity/list"

    @GetMapping("/list/filter")
    fun listFilter(model: ModelMap): String {
        model.addAttribute("entityTypeList", technologicalEntityTypeService.all.map { DropdownOption(it.id, "${it.name} (${it.shortName})") })
        model.addAttribute("userList", userService.activeAll.map { DropdownOption(it.id, it.userOfficialName) })
        return "prod/include/technological-entity/list/filter"
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val entity = id?.let { technologicalEntityService.read(id) ?: throw AlertUIException("Технологическая документация не найдена") }
        data class ProductItem(
            val id: Long?,
            val conditionalName: String?,
            val decimalNumber: String?
        )
        val jsonString = jsonMapper.writeValueAsString(
            entity?.technologicalEntityApplicabilityList?.map { ProductItem(it.product?.id, it.product?.conditionalName, it.product?.decimalNumber) } ?: emptyList<ProductItem>()
        )
        model.addAttribute("productApplicabilityList", jsonString)
        model.addAttribute("id", entity?.id)
        model.addAttribute("entityTypeId", entity?.entityType?.id)
        model.addAttribute("entityNumber", entity?.entityNumber)
        model.addAttribute("setNumber", entity?.setNumber)
        model.addAttribute("productLetterId", entity?.productLetter?.id)

        val reconciliation = entity?.technologicalEntityReconciliation
        model.addAttribute("approvedById", reconciliation?.approvedBy?.id)
        model.addAttribute("approved", reconciliation?.approved)
        model.addAttribute("designedById", reconciliation?.designedBy?.id)
        model.addAttribute("designed", reconciliation?.designed)
        model.addAttribute("checkedById", reconciliation?.checkedBy?.id)
        model.addAttribute("checked", reconciliation?.checked)
        model.addAttribute("metrologistById", reconciliation?.metrologistBy?.id)
        model.addAttribute("metrologist", reconciliation?.metrologist)
        model.addAttribute("normocontrollerById", reconciliation?.normocontrollerBy?.id)
        model.addAttribute("normocontroller", reconciliation?.normocontroller)
        model.addAttribute("militaryById", reconciliation?.militaryBy?.id)
        model.addAttribute("military", reconciliation?.military)
        model.addAttribute("technologicalChiefById", reconciliation?.technologicalChiefBy?.id)
        model.addAttribute("technologicalChief", reconciliation?.technologicalChief)
        data class AgreedItem(
            val id: Long?,
            val value: Boolean
        )
        model.addAttribute("agreedList", reconciliation?.agreedList?.map { AgreedItem(it.agreedBy?.id, it.agreed) })
        // списки
        model.addAttribute("userList", userService.activeAll.map { DropdownOption(it.id, it.userOfficialName) })
        data class EntityTypeItem(
            val id: Long?,
            val fullName: String?,
            val multi: Boolean?
        )
        val entityTypeList = technologicalEntityTypeService.all.map {
            EntityTypeItem(it.id, "${it.name} (${it.shortName})", it.multi)
        }
        model.addAttribute("entityTypeList", entityTypeList)
        model.addAttribute("productLetterList", productLetterService.all.map { DropdownOption(it.id, it.name) })
        return "prod/include/technological-entity/list/edit"
    }

    // Добавление применяемости
    @GetMapping("/list/edit/add-product")
    fun listEditAddProduct(
        model: ModelMap,
        @RequestParam productApplicabilityIdList: String
    ): String {
        model.addAttribute("productApplicabilityIdList", productApplicabilityIdList)
        return "prod/include/technological-entity/list/edit/addProduct"
    }

    @GetMapping("/list/edit/add-product/filter")
    fun listEditAddProductFilter() = "prod/include/technological-entity/list/edit/add-product/filter"

    // Окно списка применяемостей
    @GetMapping("/list/applicability")
    fun listApplicability(model: ModelMap, id: Long): String {
        data class Item(
            val id: Long?,
            val name: String?,
            val decimalNumber: String?
        )
        val entity = technologicalEntityService.read(id)
        model.addAttribute("applicabilityList", jsonMapper.writeValueAsString(
            entity.technologicalEntityApplicabilityList.map {
                Item(it.product?.id, it.product?.conditionalName, it.product?.decimalNumber)
            })
        )
        model.addAttribute("multi", entity.entityType?.multi)
        return "prod/include/technological-entity/list/applicability"
    }

    // Детальная информация
    @GetMapping("/detail")
    fun detail(model: ModelMap, id: Long): String {
        val entity = id.let { technologicalEntityService.read(it) ?: throw AlertUIException("Технологическая документация не найдена") }
        model.addAttribute("entityId", entity.id)
        model.addAttribute("entityName", "${entity.entityNumber} (${entity.setNumber})")
        return "prod/include/technological-entity/detail"
    }

    @GetMapping("/detail/edit")
    fun detailEdit(
        model: ModelMap,
        entityId: Long?,
        parentId: Long?,
        id: Long?
    ): String {
        val functionality = id?.let { functionalityService.read(it) ?: throw AlertUIException("Функциональность не найдена") }
        functionality?.let { func ->
            model.addAttribute("id", func.id)
            model.addAttribute("symbol", func.serviceSymbol?.code)
        }
        entityId?.let {
            model.addAttribute("entityId", entityId)
            model.addAttribute("serviceSymbolTypeList", values().filter { it.code == A.code })
            if (id == null) {
                val aFunctionalityList = functionalityService.getAllByCode(entityId, A.code)
                model.addAttribute("number", nextOperationNumber(aFunctionalityList))
            }
        }
        parentId?.let {
            model.addAttribute("parentId", parentId)
            var serviceSymbolTypeList = values().filter { it.code != A.code }
            val exists = functionalityService.exists(parentId, B.code)
            if (exists) serviceSymbolTypeList = serviceSymbolTypeList.filter { it.code != B.code }
            model.addAttribute("serviceSymbolTypeList", serviceSymbolTypeList)
        }

        val aFunctionality = functionality?.aFunctionality
        data class AreaItem(
            val id: Long?,
            val code: String,
            val name: String
        )
        val areaList = aFunctionality?.productionAreaList
        val areaItemList = areaList?.map { AreaItem(it.id, it.formatCode, it.name) }
        model.addAttribute("productionAreaList", areaItemList?.let { jsonMapper.writeValueAsString(it) } ?: "[]")

        model.addAttribute("workTypeList", workTypeService.all.map { DropdownOption(it.id, it.name) })
        model.addAttribute("workTypeId", aFunctionality?.workType?.id)

        model.addAttribute("nameComment", aFunctionality?.nameComment)
        model.addAttribute("description", aFunctionality?.description)

        data class LaborItem(
            val id: Long?,
            val name: String?
        )
        val laborList = aFunctionality?.laborProtectionInstructionList
        val laborItemList = laborList?.map { LaborItem(it.id, "ИОТ №${it.number} ${it.name}") }
        model.addAttribute("laborList", laborItemList?.let { jsonMapper.writeValueAsString(it) } ?: "[]")

        val bFunctionality = functionality?.bFunctionality
        data class EquipmentItem(
            val id: Long?,
            val name: String,
            val model: String?
        )
        val equipmentItemList = bFunctionality?.equipmentList?.map { EquipmentItem(it.id, it.name, it.model) }
        model.addAttribute("equipmentList", equipmentItemList?.let { jsonMapper.writeValueAsString(it) } ?: "[]")
        model.addAttribute("koid", bFunctionality?.koid)
        model.addAttribute("ksht", bFunctionality?.ksht)
        model.addAttribute("tpz", bFunctionality?.tpz)
        model.addAttribute("tsht", bFunctionality?.tsht)

        val oFunctionality = functionality?.oFunctionality
        model.addAttribute("content", oFunctionality?.content)

        if (functionality?.serviceSymbol?.code == R.code) {
            val modeList = entityId?.let { currentEntityId ->
                functionalityService.getAllByCode(currentEntityId, R.code).map { it.rFunctionality?.mode }
            } ?: listOf(functionality.rFunctionality?.mode)
            model.addAttribute("modeList", modeList)
        }

        val tFunctionality = functionality?.tFunctionality
        data class ToolItem(
            val id: Long?,
            val sign: String?,
            val name: String
        )
        val toolItemList = tFunctionality?.technologicalToolList?.map { ToolItem(it.id, it.sign, it.name) }
        model.addAttribute("technologicalToolList", toolItemList?.let { jsonMapper.writeValueAsString(it) } ?: "[]")

        val mFunctionality = functionality?.mFunctionality
        data class MaterialItem(
            val id: Long?,
            val name: String
        )
        val materialItemList = mFunctionality?.operationMaterialList?.map { MaterialItem(it.id, it.name) }
        model.addAttribute("operationMaterialList", materialItemList?.let { jsonMapper.writeValueAsString(it) } ?: "[]")

        return "prod/include/technological-entity/detail/edit"
    }

    @GetMapping("/detail/child")
    fun detailChild(
        model: ModelMap,
        id: Long
    ): String {
        val functionality = functionalityService.read(id)
        model.addAttribute("parentId", functionality.id)
        model.addAttribute("entityId", functionality.technologicalEntity?.id)
        return "prod/include/technological-entity/detail/child"
    }

    @GetMapping("/detail/labour")
    fun detailLabour(
        model: ModelMap,
        entityId: Long
    ): String {
        model.addAttribute("entityId", entityId)
        return "prod/include/technological-entity/detail/labour"
    }

    @GetMapping("/detail/edit/add-area")
    fun detailEditAddArea(
        model: ModelMap,
        @RequestParam productionAreaIdList: List<Long>
    ): String {
        model.addAttribute("areaIdList", jsonMapper.writeValueAsString(productionAreaIdList))
        return "prod/include/technological-entity/detail/edit/addArea"
    }

    @GetMapping("/detail/edit/add-labor")
    fun detailEditAddLabor(
        model: ModelMap,
        @RequestParam laborIdList: List<Long>
    ): String {
        model.addAttribute("laborIdList", jsonMapper.writeValueAsString(laborIdList))
        return "prod/include/technological-entity/detail/edit/addLabor"
    }

    @GetMapping("/detail/edit/add-equipment")
    fun detailEditAddEquipment(
        model: ModelMap,
        @RequestParam equipmentIdList: List<Long>
    ): String {
        model.addAttribute("equipmentIdList", jsonMapper.writeValueAsString(equipmentIdList))
        return "prod/include/technological-entity/detail/edit/addEquipment"
    }

    @GetMapping("/detail/edit/add-material")
    fun detailEditAddMaterial(
        model: ModelMap,
        @RequestParam materialIdList: List<Long>,
        @RequestParam parentId: Long
    ): String {
        model.addAttribute("materialIdList", jsonMapper.writeValueAsString(materialIdList))
        model.addAttribute("parentId", parentId)
        return "prod/include/technological-entity/detail/edit/addMaterial"
    }

    @GetMapping("/detail/edit/add-tool")
    fun detailEditAddTool(
        model: ModelMap,
        @RequestParam toolIdList: List<Long>
    ): String {
        model.addAttribute("toolIdList", jsonMapper.writeValueAsString(toolIdList))
        return "prod/include/technological-entity/detail/edit/addTool"
    }

    /**
     * Метод для вычисления номера операции
     */
    private fun nextOperationNumber(list: List<Functionality>): String {
        if (list.isNotEmpty()) {
            list.first().aFunctionality?.number?.toInt()?.let {
                return CommonUtil.formatZero(((it - it%5) + 5).toString(), 3)
            }
        }
        return "005"
    }
}
