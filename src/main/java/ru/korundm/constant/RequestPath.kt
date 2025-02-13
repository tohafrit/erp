package ru.korundm.constant

/**
 * Хранилище констант путей запросов
 * @author mazur_ea
 * Date:   09.11.2020
 */
object RequestPath {

    const val CORP = "/corp"
    const val PROD = "/prod"

    object Section {
        const val INDEX = "index"
        const val NEWS = "news"
        const val NEWS_EDITION = "news-edition"
        const val COMPANY_DETAIL = "company-detail"
        const val ADMINISTRATION_OFFICE_DEMAND = "administration-office-demand"
        const val GRATITUDE = "gratitude"
        const val TRIP = "trip"
        const val OFFICE_SUPPLY = "office-supply"
        const val CORPORATE_DOCUMENT_CATEGORY = "corporate-document-category"
        const val MESSAGE_TEMPLATE = "message-template"
        const val MESSAGE_TYPE = "message-type"
        const val SUBDIVISION = "subdivision"
        const val ACCOUNT = "account"
        const val BANK = "bank"
        const val BASIC_ECONOMIC_INDICATOR = "basic-economic-indicator"
        const val CLASSIFICATION_GROUP = "classification-group"
        const val COMPANY = "company"
        const val COMPONENT = "component"
        const val COMPONENT_APPOINTMENT = "component-appointment"
        const val COMPONENT_CATEGORY = "component-category"
        const val COMPONENT_GROUP = "component-group"
        const val COMPONENT_INSTALLATION_TYPE = "component-installation-type"
        const val COMPONENT_KIND = "component-kind"
        const val COMPONENT_MARK_TYPE = "component-mark-type"
        const val COMPONENT_PURPOSE = "component-purpose"
        const val CONTRACT = "contract"
        const val DOCUMENT_LABEL = "document-label"
        const val EQUIPMENT = "equipment"
        const val EQUIPMENT_UNIT_EVENT = "equipment-unit-event"
        const val EQUIPMENT_UNIT_EVENT_TYPE = "equipment-unit-event-type"
        const val FAQ = "faq"
        const val GOVERNMENT_CONTRACT = "government-contract"
        const val LABOR_PROTECTION_INSTRUCTION = "labor-protection-instruction"
        const val PRODUCT_LETTER = "product-letter"
        const val OKEI = "okei"
        const val OKPD_CODE = "okpd-code"
        const val OPERATION_MATERIAL = "operation-material"
        const val PRESENT_LOG_RECORD = "present-log-record"
        const val PRINTER = "printer"
        const val PRODUCER = "producer"
        const val PRODUCT = "product"
        const val PRODUCT_TYPE = "product-type"
        const val PRODUCTION_AREA = "production-area"
        const val PRODUCTION_SHIPMENT_LETTER = "production-shipment-letter"
        const val PRODUCTION_WAREHOUSE = "production-warehouse"
        const val REASON_CHANGE = "reason-change"
        const val SERVICE_SYMBOL = "service-symbol"
        const val SERVICE_TYPE = "service-type"
        const val SUPPLIER = "supplier"
        const val TECHNOLOGICAL_ENTITY = "technological-entity"
        const val TECHNOLOGICAL_ENTITY_NOTIFICATION = "technological-entity-notification"
        const val TECHNOLOGICAL_ENTITY_TYPE = "technological-entity-type"
        const val TECHNOLOGICAL_TOOL = "technological-tool"
        const val VALUE_ADDED_TAX = "value-added-tax"
        const val WAREHOUSE_STATE = "warehouse-state"
        const val WORK_TYPE = "work-type"
        const val LAUNCH = "launch"
        const val LAUNCH_NOTE = "launch-note"
        const val PRODUCT_PRICE_PERIOD = "product-price-period"
        const val PRODUCT_WORK_COST_JUSTIFICATION = "product-work-cost-justification"
        const val PRODUCT_SPEC_REVIEW_JUSTIFICATION = "product-spec-review-justification"
        const val PRODUCT_SPEC_RESEARCH_JUSTIFICATION = "product-spec-research-justification"
        const val PRODUCT_LABOUR_INTENSITY = "product-labour-intensity"
        const val INTERNAL_WAYBILL = "internal-waybill"
        const val SHIPMENT_WAYBILL = "shipment-waybill"
        const val CONSTRUCTOR_DOCUMENT_NOTIFICATION = "constructor-document-notification"
        const val IMPORTATION_DATA = "importation-data"
        const val PURCHASE_PLAN_PERIOD = "purchase-plan-period"
        const val PURCHASE_PLAN = "purchase-plan"
    }

    object View {

        private const val API = "/api/view"

        object Corp {
            private const val PREF = "$API$CORP/"
            const val INDEX = "$PREF${Section.INDEX}"
            const val NEWS = "$PREF${Section.NEWS}"
            const val NEWS_EDITION = "$PREF${Section.NEWS_EDITION}"
            const val COMPANY_DETAIL = "$PREF${Section.COMPANY_DETAIL}"
            const val ADMINISTRATION_OFFICE_DEMAND = "$PREF${Section.ADMINISTRATION_OFFICE_DEMAND}"
            const val GRATITUDE = "$PREF${Section.GRATITUDE}"
            const val TRIP = "$PREF${Section.TRIP}"
            const val OFFICE_SUPPLY = "$PREF${Section.OFFICE_SUPPLY}"
            const val CORPORATE_DOCUMENT_CATEGORY = "$PREF${Section.CORPORATE_DOCUMENT_CATEGORY}"
            const val MESSAGE_TEMPLATE = "$PREF${Section.MESSAGE_TEMPLATE}"
            const val MESSAGE_TYPE = "$PREF${Section.MESSAGE_TYPE}"
            const val SUBDIVISION = "$PREF${Section.SUBDIVISION}"
        }

        object Prod {
            private const val PREF = "$API$PROD/"
            const val ACCOUNT = "$PREF${Section.ACCOUNT}"
            const val BANK = "$PREF${Section.BANK}"
            const val BASIC_ECONOMIC_INDICATOR = "$PREF${Section.BASIC_ECONOMIC_INDICATOR}"
            const val CLASSIFICATION_GROUP = "$PREF${Section.CLASSIFICATION_GROUP}"
            const val COMPANY = "$PREF${Section.COMPANY}"
            const val COMPONENT = "$PREF${Section.COMPONENT}"
            const val COMPONENT_APPOINTMENT = "$PREF${Section.COMPONENT_APPOINTMENT}"
            const val COMPONENT_CATEGORY = "$PREF${Section.COMPONENT_CATEGORY}"
            const val COMPONENT_GROUP = "$PREF${Section.COMPONENT_GROUP}"
            const val COMPONENT_INSTALLATION_TYPE = "$PREF${Section.COMPONENT_INSTALLATION_TYPE}"
            const val COMPONENT_KIND = "$PREF${Section.COMPONENT_KIND}"
            const val COMPONENT_MARK_TYPE = "$PREF${Section.COMPONENT_MARK_TYPE}"
            const val COMPONENT_PURPOSE = "$PREF${Section.COMPONENT_PURPOSE}"
            const val CONTRACT = "$PREF${Section.CONTRACT}"
            const val DOCUMENT_LABEL = "$PREF${Section.DOCUMENT_LABEL}"
            const val EQUIPMENT = "$PREF${Section.EQUIPMENT}"
            const val EQUIPMENT_UNIT_EVENT = "$PREF${Section.EQUIPMENT_UNIT_EVENT}"
            const val EQUIPMENT_UNIT_EVENT_TYPE = "$PREF${Section.EQUIPMENT_UNIT_EVENT_TYPE}"
            const val FAQ = "$PREF${Section.FAQ}"
            const val GOVERNMENT_CONTRACT = "$PREF${Section.GOVERNMENT_CONTRACT}"
            const val LABOR_PROTECTION_INSTRUCTION = "$PREF${Section.LABOR_PROTECTION_INSTRUCTION}"
            const val PRODUCT_LETTER = "$PREF${Section.PRODUCT_LETTER}"
            const val OKEI = "$PREF${Section.OKEI}"
            const val OKPD_CODE = "$PREF${Section.OKPD_CODE}"
            const val OPERATION_MATERIAL = "$PREF${Section.OPERATION_MATERIAL}"
            const val PRESENT_LOG_RECORD = "$PREF${Section.PRESENT_LOG_RECORD}"
            const val PRINTER = "$PREF${Section.PRINTER}"
            const val PRODUCER = "$PREF${Section.PRODUCER}"
            const val PRODUCT = "$PREF${Section.PRODUCT}"
            const val PRODUCT_TYPE = "$PREF${Section.PRODUCT_TYPE}"
            const val PRODUCTION_AREA = "$PREF${Section.PRODUCTION_AREA}"
            const val PRODUCTION_SHIPMENT_LETTER = "$PREF${Section.PRODUCTION_SHIPMENT_LETTER}"
            const val PRODUCTION_WAREHOUSE = "$PREF${Section.PRODUCTION_WAREHOUSE}"
            const val REASON_CHANGE = "$PREF${Section.REASON_CHANGE}"
            const val SERVICE_SYMBOL = "$PREF${Section.SERVICE_SYMBOL}"
            const val SERVICE_TYPE = "$PREF${Section.SERVICE_TYPE}"
            const val SUPPLIER = "$PREF${Section.SUPPLIER}"
            const val TECHNOLOGICAL_ENTITY = "$PREF${Section.TECHNOLOGICAL_ENTITY}"
            const val TECHNOLOGICAL_ENTITY_NOTIFICATION = "$PREF${Section.TECHNOLOGICAL_ENTITY_NOTIFICATION}"
            const val TECHNOLOGICAL_ENTITY_TYPE = "$PREF${Section.TECHNOLOGICAL_ENTITY_TYPE}"
            const val TECHNOLOGICAL_TOOL = "$PREF${Section.TECHNOLOGICAL_TOOL}"
            const val VALUE_ADDED_TAX = "$PREF${Section.VALUE_ADDED_TAX}"
            const val WAREHOUSE_STATE = "$PREF${Section.WAREHOUSE_STATE}"
            const val WORK_TYPE = "$PREF${Section.WORK_TYPE}"
            const val LAUNCH = "$PREF${Section.LAUNCH}"
            const val LAUNCH_NOTE = "$PREF${Section.LAUNCH_NOTE}"
            const val PRODUCT_PRICE_PERIOD = "$PREF${Section.PRODUCT_PRICE_PERIOD}"
            const val PRODUCT_WORK_COST_JUSTIFICATION = "$PREF${Section.PRODUCT_WORK_COST_JUSTIFICATION}"
            const val PRODUCT_SPEC_REVIEW_JUSTIFICATION = "$PREF${Section.PRODUCT_SPEC_REVIEW_JUSTIFICATION}"
            const val PRODUCT_SPEC_RESEARCH_JUSTIFICATION = "$PREF${Section.PRODUCT_SPEC_RESEARCH_JUSTIFICATION}"
            const val PRODUCT_LABOUR_INTENSITY = "$PREF${Section.PRODUCT_LABOUR_INTENSITY}"
            const val INTERNAL_WAYBILL = "$PREF${Section.INTERNAL_WAYBILL}"
            const val SHIPMENT_WAYBILL = "$PREF${Section.SHIPMENT_WAYBILL}"
            const val CONSTRUCTOR_DOCUMENT_NOTIFICATION = "$PREF${Section.CONSTRUCTOR_DOCUMENT_NOTIFICATION}"
            const val IMPORTATION_DATA = "$PREF${Section.IMPORTATION_DATA}"
            const val PURCHASE_PLAN_PERIOD = "$PREF${Section.PURCHASE_PLAN_PERIOD}"
            const val PURCHASE_PLAN = "$PREF${Section.PURCHASE_PLAN}"
        }
    }

    object Action {

        private const val API = "/api/action"

        object Corp {
            private const val PREF = "$API$CORP/"
            const val INDEX = "$PREF${Section.INDEX}"
            const val NEWS = "$PREF${Section.NEWS}"
            const val NEWS_EDITION = "$PREF${Section.NEWS_EDITION}"
            const val COMPANY_DETAIL = "$PREF${Section.COMPANY_DETAIL}"
            const val ADMINISTRATION_OFFICE_DEMAND = "$PREF${Section.ADMINISTRATION_OFFICE_DEMAND}"
            const val GRATITUDE = "$PREF${Section.GRATITUDE}"
            const val TRIP = "$PREF${Section.TRIP}"
            const val OFFICE_SUPPLY = "$PREF${Section.OFFICE_SUPPLY}"
            const val CORPORATE_DOCUMENT_CATEGORY = "$PREF${Section.CORPORATE_DOCUMENT_CATEGORY}"
            const val MESSAGE_TEMPLATE = "$PREF${Section.MESSAGE_TEMPLATE}"
            const val MESSAGE_TYPE = "$PREF${Section.MESSAGE_TYPE}"
            const val SUBDIVISION = "$PREF${Section.SUBDIVISION}"
        }

        object Prod {
            private const val PREF = "$API$PROD/"
            const val ACCOUNT = "$PREF${Section.ACCOUNT}"
            const val BANK = "$PREF${Section.BANK}"
            const val BASIC_ECONOMIC_INDICATOR = "$PREF${Section.BASIC_ECONOMIC_INDICATOR}"
            const val CLASSIFICATION_GROUP = "$PREF${Section.CLASSIFICATION_GROUP}"
            const val COMPANY = "$PREF${Section.COMPANY}"
            const val COMPONENT = "$PREF${Section.COMPONENT}"
            const val COMPONENT_APPOINTMENT = "$PREF${Section.COMPONENT_APPOINTMENT}"
            const val COMPONENT_CATEGORY = "$PREF${Section.COMPONENT_CATEGORY}"
            const val COMPONENT_GROUP = "$PREF${Section.COMPONENT_GROUP}"
            const val COMPONENT_INSTALLATION_TYPE = "$PREF${Section.COMPONENT_INSTALLATION_TYPE}"
            const val COMPONENT_KIND = "$PREF${Section.COMPONENT_KIND}"
            const val COMPONENT_MARK_TYPE = "$PREF${Section.COMPONENT_MARK_TYPE}"
            const val COMPONENT_PURPOSE = "$PREF${Section.COMPONENT_PURPOSE}"
            const val CONTRACT = "$PREF${Section.CONTRACT}"
            const val DOCUMENT_LABEL = "$PREF${Section.DOCUMENT_LABEL}"
            const val EQUIPMENT = "$PREF${Section.EQUIPMENT}"
            const val EQUIPMENT_UNIT_EVENT = "$PREF${Section.EQUIPMENT_UNIT_EVENT}"
            const val EQUIPMENT_UNIT_EVENT_TYPE = "$PREF${Section.EQUIPMENT_UNIT_EVENT_TYPE}"
            const val FAQ = "$PREF${Section.FAQ}"
            const val GOVERNMENT_CONTRACT = "$PREF${Section.GOVERNMENT_CONTRACT}"
            const val LABOR_PROTECTION_INSTRUCTION = "$PREF${Section.LABOR_PROTECTION_INSTRUCTION}"
            const val PRODUCT_LETTER = "$PREF${Section.PRODUCT_LETTER}"
            const val OKEI = "$PREF${Section.OKEI}"
            const val OKPD_CODE = "$PREF${Section.OKPD_CODE}"
            const val OPERATION_MATERIAL = "$PREF${Section.OPERATION_MATERIAL}"
            const val PRESENT_LOG_RECORD = "$PREF${Section.PRESENT_LOG_RECORD}"
            const val PRINTER = "$PREF${Section.PRINTER}"
            const val PRODUCER = "$PREF${Section.PRODUCER}"
            const val PRODUCT = "$PREF${Section.PRODUCT}"
            const val PRODUCT_TYPE = "$PREF${Section.PRODUCT_TYPE}"
            const val PRODUCTION_AREA = "$PREF${Section.PRODUCTION_AREA}"
            const val PRODUCTION_SHIPMENT_LETTER = "$PREF${Section.PRODUCTION_SHIPMENT_LETTER}"
            const val PRODUCTION_WAREHOUSE = "$PREF${Section.PRODUCTION_WAREHOUSE}"
            const val REASON_CHANGE = "$PREF${Section.REASON_CHANGE}"
            const val SERVICE_SYMBOL = "$PREF${Section.SERVICE_SYMBOL}"
            const val SERVICE_TYPE = "$PREF${Section.SERVICE_TYPE}"
            const val SUPPLIER = "$PREF${Section.SUPPLIER}"
            const val TECHNOLOGICAL_ENTITY = "$PREF${Section.TECHNOLOGICAL_ENTITY}"
            const val TECHNOLOGICAL_ENTITY_NOTIFICATION = "$PREF${Section.TECHNOLOGICAL_ENTITY_NOTIFICATION}"
            const val TECHNOLOGICAL_ENTITY_TYPE = "$PREF${Section.TECHNOLOGICAL_ENTITY_TYPE}"
            const val TECHNOLOGICAL_TOOL = "$PREF${Section.TECHNOLOGICAL_TOOL}"
            const val VALUE_ADDED_TAX = "$PREF${Section.VALUE_ADDED_TAX}"
            const val WAREHOUSE_STATE = "$PREF${Section.WAREHOUSE_STATE}"
            const val WORK_TYPE = "$PREF${Section.WORK_TYPE}"
            const val LAUNCH = "$PREF${Section.LAUNCH}"
            const val LAUNCH_NOTE = "$PREF${Section.LAUNCH_NOTE}"
            const val PRODUCT_PRICE_PERIOD = "$PREF${Section.PRODUCT_PRICE_PERIOD}"
            const val PRODUCT_WORK_COST_JUSTIFICATION = "$PREF${Section.PRODUCT_WORK_COST_JUSTIFICATION}"
            const val PRODUCT_SPEC_REVIEW_JUSTIFICATION = "$PREF${Section.PRODUCT_SPEC_REVIEW_JUSTIFICATION}"
            const val PRODUCT_SPEC_RESEARCH_JUSTIFICATION = "$PREF${Section.PRODUCT_SPEC_RESEARCH_JUSTIFICATION}"
            const val PRODUCT_LABOUR_INTENSITY = "$PREF${Section.PRODUCT_LABOUR_INTENSITY}"
            const val INTERNAL_WAYBILL = "$PREF${Section.INTERNAL_WAYBILL}"
            const val SHIPMENT_WAYBILL = "$PREF${Section.SHIPMENT_WAYBILL}"
            const val CONSTRUCTOR_DOCUMENT_NOTIFICATION = "$PREF${Section.CONSTRUCTOR_DOCUMENT_NOTIFICATION}"
            const val IMPORTATION_DATA = "$PREF${Section.IMPORTATION_DATA}"
            const val PURCHASE_PLAN_PERIOD = "$PREF${Section.PURCHASE_PLAN_PERIOD}"
            const val PURCHASE_PLAN = "$PREF${Section.PURCHASE_PLAN}"
        }
    }
}