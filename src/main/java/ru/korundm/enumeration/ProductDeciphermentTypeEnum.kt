package ru.korundm.enumeration

import ru.korundm.report.excel.document.decipherment.DeciphermentExcel
import ru.korundm.report.excel.document.decipherment.report.*

/**
 * Перечисление типов расшифровок
 * @author mazur_ea
 * Date:   07.07.2021
 */
enum class ProductDeciphermentTypeEnum(
    val id: Long,
    val excelCl: Class<out DeciphermentExcel>? // класс генерации excel-файла
) {

    FORM_1(1, Form1Excel::class.java), // Форма № 1 – Протокол цены единицы продукции и выбора вида цены
    FORM_2(2, Form2or3Excel::class.java), // Форма № 2 – Плановая калькуляция затрат
    FORM_3(3, Form2or3Excel::class.java), // Форма № 3 – Отчетная калькуляция
    FORM_4(4, RawMaterialCostDeciphermentExcel::class.java), // Форма № 4 – Затраты на приобретение сырья, материалов и вспомогательных материалов
    FORM_5(5, SimpleFormExcel::class.java), // Форма № 5 – Затраты на приобретение полуфабрикатов
    FORM_6_1(6, PurchasedComponentCostDeciphermentExcel::class.java), // Форма № 6.1 – Затраты на приобретение комплектующих изделий
    FORM_6_2(7, TareAndPackagingCostDeciphermentExcel::class.java), // Форма № 6.2 – Затраты на приобретение комплектующих изделий (тара и упаковка)
    FORM_6_3(8, OwnProductionProductCostDeciphermentExcel::class.java), // Форма № 6.3 – Затраты на изделия собственного производства
    FORM_7(9, SimpleFormExcel::class.java), // Форма № 7 – Затраты на оплату работ и услуг сторонних организаций производственного характера
    FORM_7_1(10, SimpleFormExcel::class.java), // Форма № 7.1 – Затраты по работам (услугам), выполняемым (оказываемым) сторонними организациями
    FORM_8(11, SimpleFormExcel::class.java), // Форма № 8 (8д) – Расчет-обоснование норматива транспортно-заготовительных затрат
    FORM_9(12, Form9Excel::class.java), // Форма № 9 – Основная заработная плата
    FORM_10(21, SimpleFormExcel::class.java), // Форма № 10 – Расчет-обоснование уровня дополнительной заработной платы основных работников
    FORM_11(22, SimpleFormExcel::class.java), // Форма № 11 – Смета и расчет общепроизводственных затрат
    FORM_12(23, SimpleFormExcel::class.java), // Форма № 12 – Смета и расчет общехозяйственных затрат / административно - управленческих расходов
    FORM_13(24, SimpleFormExcel::class.java), // Форма № 13 –
    FORM_14(13, SimpleFormExcel::class.java), // Форма № 14 – Специальные затраты
    FORM_14_1(14, SimpleFormExcel::class.java), // Форма № 14.1 (14д) – Затраты на испытания
    FORM_16(15, SimpleFormExcel::class.java), // Форма № 16 – Затраты на специальную технологическую оснастку
    FORM_17(16, SimpleFormExcel::class.java), // Форма № 17 (17д) – Затраты на подготовку и освоение производства
    FORM_18(17, Form18Excel::class.java), // Форма № 18 (18д) – Прочие прямые затраты
    FORM_19(18, SimpleFormExcel::class.java), // Форма № 19 – Затраты на командировки
    FORM_20(19, Form20Excel::class.java), // Форма № 20 – Расчет и обоснование прибыли
    FORM_21(25, SimpleFormExcel::class.java), // Форма № 21 – Расчет и обоснование прибыли
    FORM_22(26, SimpleFormExcel::class.java), // Форма № 22 – Расчет и обоснование прибыли
    FORM_23(27, SimpleFormExcel::class.java), // Форма № 23 – Расчет и обоснование прибыли
    EXPLANATION_NOTE(20, SimpleFormExcel::class.java); // Пояснительная записка

    companion object {
        fun getById(id: Long?) = values().first { it.id == id }
    }
}