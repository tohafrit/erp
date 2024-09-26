package ru.korundm.enumeration

enum class DocumentationType(
    val id: Long,
    val property: String
) {

    CONTRACT(1, "Текст договора"),
    EXECUTION_STATEMENT(2, "Ведомость исполнения"),
    COVERING_CUSTOMER_LETTER(3, "Сопроводительное письмо Заказчику"),
    DISPUTE_RECONCILIATION_PROTOCOL(4, "Протокол согласования разногласий"),
    LETTER_DISPUTE_RECONCILIATION_PROTOCOL(5, "Письмо Заказчику с протоколом согласования разногласий"),
    SIMPLE_LETTER(6, "Простое письмо"),
    CUSTOMER_LETTER_PRICE_PROTOCOL(7, "Письмо Заказчику с документами (протокол цены)"),
    CUSTOMER_LETTER_DEED(8, "Письмо Заказчику с документами (акт)"),
    COVERING_LETTER_PZ(9, "Сопроводительное письмо в ПЗ"),
    LETTER_PZ_COPY(10, "Письмо в ПЗ (копия)"),
    LETTER_PZ_DISPUTE_RECONCILIATION_PROTOCOL(11, "Письмо в ПЗ с протоколами согласования разногласий"),
    LETTER_PZ_PRICE_PROTOCOL(12, "Письмо в ПЗ с протоколом цены"),
    SERVICE_NOTE_ACCOUNTING(13, "Служебная записка в бухгалтерию (оригинал)"),
    SERVICE_NOTE_ACCOUNTING_DEED(14, "Служебная записка в бухгалтерию (оригинал акта)"),
    SERVICE_NOTE_ACCOUNTING_DEED_REQUEST(15, "Служебная записка в бухгалтерию (запрос на акт)"),
    DEED(16, "Акт приемки-передачи продукции"),
    DEED_SP_SI(17, "Акт сдачи-приемки работ по СП и СИ"),
    SHIPPING_AUTHORIZATION(18, "Разрешение на отгрузку"),
    SERVICE_NOTE_TECHNOLOGIST(19, "Служебная записка технологам (запрос на трудоемкость)"),
    SERVICE_NOTE_WARRANTY(20, "Служебная записка о дополнительной гарантии"),
    COVERING_CUSTOMER_LETTER_INVOICE(21, "Сопроводительное письмо к счету фактуре"),
    CUSTOMER_LETTER_POSTPONEMENT(22, "Письмо о переносе сроков"),
    CUSTOMER_LETTER_ADVANCE_PAYMENT(23, "Письмо на оплату аванса"),
    CUSTOMER_LETTER_PAYMENT(24, "Письмо на окончательную оплату"),
    CUSTOMER_LETTER_INCORRECT_PAYMENT_ORDERS(25, "Письмо неправильные платежные поручения"),
    LETTER_WAREHOUSE(26, "Письмо на склад"),
    SERVICE_NOTE(27, "Служебная записка"),
    DISPOSITION(28, "Распоряжение"),
    MSN(29, "МСН"),
    WAYBILL(30, "Накладная на отгрузку"),
    WAREHOUSE_ACT(31, "Акт для отгрузки"),
    WAREHOUSE_RESIDUE(32, ""),
    WAREHOUSE_RECEIPT_SHIPMENT_PRODUCT_PERIOD(33, ""),
    WAREHOUSE_MONTHLY_SHIPMENT_REPORT(34, "");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }
}