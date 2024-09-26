package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class CompanyTypeEnum(
    val id: Long,
    val property: String
) : EnumConvertible<Long> {

    KORUND_M(1, "companyType.korundM"),
    SAPSAN(2, "companyType.sapsan"),
    CUSTOMERS(4, "companyType.customers"),
    CONTRACTORS(8, "companyType.contractors"),
    NIISI(16, "companyType.niisi"),
    OAO_KORUND_M(32, "companyType.oaoKorundM"),
    PRODUCT_PRODUCER(64, "companyType.productProducer"),
    COMPONENT_PRODUCER(128, "companyType.componentProducer");

    companion object {
        /** Список типов, которые нельзя использовать в привязке  */
        private val EXCLUDE_EDIT_LIST = listOf(KORUND_M, SAPSAN, NIISI, OAO_KORUND_M)

        fun getById(id: Long) = values().first { it.id == id }
        fun getAllowed() = values().filter { !EXCLUDE_EDIT_LIST.contains(it) }.toList()
    }

    override fun toValue()  = id

    @Converter
    class CustomConverter : EnumConverter<CompanyTypeEnum, Long>(CompanyTypeEnum::class.java)
}