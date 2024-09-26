package ru.korundm.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import ru.korundm.constant.RequestPath
import ru.korundm.dao.AppSettingsService
import ru.korundm.entity.AppSettings
import ru.korundm.enumeration.AppSettingsAttr.PRODUCT_PRICE_UNLOCK_TIME
import ru.korundm.enumeration.CompanyTypeEnum
import ru.korundm.enumeration.ComponentLifecycle
import java.time.LocalDate

@Controller
@RequestMapping(RequestPath.PROD)
class ViewPathProdController(
    private val appSettingsService: AppSettingsService
) {

    @GetMapping("/index")
    fun prodIndex() = "prod/index"

    @GetMapping("/bank")
    fun bank() = "prod/section/bank"

    @GetMapping("/classification-group")
    fun classificationGroup() = "prod/section/classificationGroup"

    @GetMapping("/company-korund")
    fun companyKorund(model: ModelMap): String {
        model.addAttribute("typeId", CompanyTypeEnum.OAO_KORUND_M.id)
        return "prod/section/company"
    }

    @GetMapping("/company-customer")
    fun companyCustomer(model: ModelMap): String {
        model.addAttribute("typeId", CompanyTypeEnum.CUSTOMERS.id)
        return "prod/section/company"
    }

    @GetMapping("/company-product-producer")
    fun companyProductProducer(model: ModelMap): String {
        model.addAttribute("typeId", CompanyTypeEnum.PRODUCT_PRODUCER.id)
        return "prod/section/company"
    }

    @GetMapping("/company-contractor")
    fun companyContractor(model: ModelMap): String {
        model.addAttribute("typeId", CompanyTypeEnum.CONTRACTORS.id)
        return "prod/section/company"
    }

    @GetMapping("/company-component-producer")
    fun companyComponentProducer(model: ModelMap): String {
        model.addAttribute("typeId", CompanyTypeEnum.COMPONENT_PRODUCER.id)
        return "prod/section/company"
    }

    @GetMapping("/component-new")
    fun componentNew(model: ModelMap): String {
        model.addAttribute("lifecycleId", ComponentLifecycle.NEW.id)
        return "prod/section/component"
    }

    @GetMapping("/component-design")
    fun componentDesign(model: ModelMap): String {
        model.addAttribute("lifecycleId", ComponentLifecycle.DESIGN.id)
        return "prod/section/component"
    }

    @GetMapping("/component-industrial")
    fun componentIndustrial(model: ModelMap): String {
        model.addAttribute("lifecycleId", ComponentLifecycle.INDUSTRIAL.id)
        return "prod/section/component"
    }

    @GetMapping("/component-appointment")
    fun componentAppointment() = "prod/section/componentAppointment"

    @GetMapping("/component-category")
    fun componentCategory() = "prod/section/componentCategory"

    @GetMapping("/component-group")
    fun componentGroup() = "prod/section/componentGroup"

    @GetMapping("/component-installation-type")
    fun componentInstallationType() = "prod/section/componentInstallationType"

    @GetMapping("/component-kind")
    fun componentKind() = "prod/section/componentKind"

    @GetMapping("/component-mark-type")
    fun componentMarkType() = "prod/section/componentMarkType"

    @GetMapping("/component-purpose")
    fun componentPurpose() = "prod/section/componentPurpose"

    @GetMapping("/contract", "/contract/**")
    fun contract() = "prod/section/contract"

    @GetMapping("/equipment")
    fun equipment() = "prod/section/equipment"

    @GetMapping("/equipment-unit-event")
    fun equipmentUnitEvent() = "prod/section/equipmentUnitEvent"

    @GetMapping("/equipment-unit-event-type")
    fun equipmentUnitEventType() = "prod/section/equipmentUnitEventType"

    @GetMapping("/faq")
    fun faq() = "prod/section/faq"

    @GetMapping("/labor-protection-instruction", "/labor-protection-instruction/**")
    fun laborProtectionInstruction() = "prod/section/laborProtectionInstruction"

    @GetMapping("/product-letter")
    fun productLetter() = "prod/section/productLetter"

    @GetMapping("/okei")
    fun okei() = "prod/section/okei"

    @GetMapping("/operation-material")
    fun operationMaterial() = "prod/section/operationMaterial"

    @GetMapping("/printer")
    fun printer() = "prod/section/printer"

    @GetMapping("/producer")
    fun producer() = "prod/section/producer"

    @GetMapping("/product", "/product/**")
    fun product() = "prod/section/product"

    @GetMapping("/product-type")
    fun productType() = "prod/section/productType"

    @GetMapping("/production-area")
    fun productionArea() = "prod/section/productionArea"

    @GetMapping("/production-warehouse")
    fun productionWarehouse() = "prod/section/productionWarehouse"

    @GetMapping("/reason-change")
    fun reasonChange() = "prod/section/reasonChange"

    @GetMapping("/service-symbol")
    fun serviceSymbol() = "prod/section/serviceSymbol"

    @GetMapping("/supplier")
    fun supplier() = "prod/section/supplier"

    @GetMapping("/technological-entity", "/technological-entity/**")
    fun technologicalEntity() = "prod/section/technologicalEntity"

    @GetMapping("/technological-entity-notification", "/technological-entity-notification/**")
    fun technologicalEntityNotification() = "prod/section/technologicalEntityNotification"

    @GetMapping("/technological-entity-type", "/technological-entity-type/**")
    fun technologicalEntityType() = "prod/section/technologicalEntityType"

    @GetMapping("/technological-tool")
    fun technologicalTool() = "prod/section/technologicalTool"

    @GetMapping("/value-added-tax")
    fun valueAddedTax() = "prod/section/valueAddedTax"

    @GetMapping("/work-type")
    fun workType() = "prod/section/workType"

    @GetMapping("/launch", "/launch/**")
    fun launch() = "prod/section/launch"

    @GetMapping("/report")
    fun report() = "prod/section/report"

    @GetMapping("/acceptance")
    fun acceptance() = "prod/section/acceptance"

    @GetMapping("/launch-note", "/launch-note/**")
    fun launchNote() = "prod/section/launchNote"

    @GetMapping("/service-type", "/service-type/**")
    fun serviceType() = "prod/section/serviceType"

    @GetMapping("/product-price-period", "/product-price-period/**")
    fun productPricePeriod() = "prod/section/productPricePeriod"

    @GetMapping("/product-work-cost-justification", "/product-work-cost-justification/**")
    fun productWorkCostJustification() = "prod/section/productWorkCostJustification"

    @GetMapping("/product-spec-review-justification", "/product-spec-review-justification/**")
    fun productSpecReviewJustification() = "prod/section/productSpecReviewJustification"

    @GetMapping("/product-spec-research-justification", "/product-spec-research-justification/**")
    fun productSpecResearchJustification() = "prod/section/productSpecResearchJustification"

    @GetMapping("/okpd-code", "/okpd-code/**")
    fun okpdCode() = "prod/section/okpdCode"

    @GetMapping("/government-contract", "/government-contract/**")
    fun governmentContract() = "prod/section/governmentContract"

    @GetMapping("/basic-economic-indicator", "/basic-economic-indicator/**")
    fun basicEconomicIndicator() = "prod/section/basicEconomicIndicator"

    @GetMapping("/present-log-record", "/present-log-record/**")
    fun presentLogRecord() = "prod/section/presentLogRecord"

    @GetMapping("/product-labour-intensity", "/product-labour-intensity/**")
    fun productLabourIntensity() = "prod/section/productLabourIntensity"

    @GetMapping("/production-shipment-letter", "/production-shipment-letter/**")
    fun productionShipmentLetter() = "prod/section/productionShipmentLetter"

    @GetMapping("/account", "/account/**")
    fun account() = "prod/section/account"

    @GetMapping("/document-label", "/document-label/**")
    fun documentLabel() = "prod/section/documentLabel"

    @GetMapping("/internal-waybill", "/internal-waybill/**")
    fun internalWaybill() = "prod/section/internalWaybill"

    @GetMapping("/shipment-waybill", "/shipment-waybill/**")
    fun shipmentWaybill() = "prod/section/shipmentWaybill"

    @GetMapping("/warehouse-state", "/warehouse-state/**")
    fun warehouseState() = "prod/section/warehouseState"

    @GetMapping("/constructor-document-notification", "/constructor-document-notification/**")
    fun constructorDocumentNotification() = "prod/section/constructorDocumentNotification"

    @GetMapping("/importation-data", "/importation-data/**")
    fun importationData() = "prod/section/importationData"

    @GetMapping("/purchase-plan-period", "/purchase-plan-period/**")
    fun purchasePlanPeriod() = "prod/section/purchasePlanPeriod"

    @GetMapping("/purchase-plan", "/purchase-plan/**")
    fun purchasePlan() = "prod/section/purchasePlan"

    @GetMapping("/product-price-unlock")
    fun productPriceUnlock(): String {
        var attr = appSettingsService.findFirstByAttr(PRODUCT_PRICE_UNLOCK_TIME)
        if (attr == null) {
            attr = AppSettings()
            attr.attr = PRODUCT_PRICE_UNLOCK_TIME
        }
        attr.dateVal = LocalDate.now()
        appSettingsService.save(attr)
        return "index";
    }
}