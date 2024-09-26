package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class InvoiceStatus(
    val id: Long,
    val property: String,
    val description: String
) : EnumConvertible<Long> {

    ACTING(1, "invoiceStatus.acting", "Действующий"),
    CLOSED(2, "invoiceStatus.closed", "Закрыт"),
    CANCELED(4, "invoiceStatus.canceled", "Аннулирован");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<InvoiceStatus, Long>(InvoiceStatus::class.java)
}