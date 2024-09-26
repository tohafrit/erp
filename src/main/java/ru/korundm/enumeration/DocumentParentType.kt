package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class DocumentParentType(
    val id: Long,
    val property: String,
    // TODO в дальнейшем надо избавиться от поля code
    val code: String
) : EnumConvertible<Long> {

    CONTRACT(1, "documentParentType.contract", "Contract"),
    LAUNCH(2, "documentParentType.launch", "Launch"),
    LAUNCH_PRODUCT(4, "documentParentType.launchProduct", "LaunchProduct"),
    CONTRACT_SECTION(8, "documentParentType.contractSection", "ContractSection"),
    DOCUMENT(1024, "documentParentType.document", "Document"),
    READY_PRODUCT(2048, "documentParentType.readyDocument", "ReadyProduct"),
    COMPANY(16384, "documentParentType.company", "Company"),
    MONTHLY_PLAN(1048576, "documentParentType.monthPlan", "MonthlyPlan"),
    BOM(16777216, "documentParentType.bom", "BOM");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<DocumentParentType, Long>(DocumentParentType::class.java)
}