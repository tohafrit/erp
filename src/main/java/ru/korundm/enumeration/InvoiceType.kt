package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class InvoiceType(
    val id: Long,
    val property: String,
    val description: String
) : EnumConvertible<Long> {

    ADVANCE(1, "invoiceType.advance", "Авансовый"),
    FINAL_INVOICE(2, "invoiceType.finalInvoice", "На окончательную оплату"),
    INVOICE_FOR_AMOUNT(4, "invoiceType.invoiceForAmount", "На выставленную сумму");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<InvoiceType, Long>(InvoiceType::class.java)
}