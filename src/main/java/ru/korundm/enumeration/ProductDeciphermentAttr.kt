package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter as PersistenceConverter

/**
 * Перечисление параметров расшифровок цены изделия
 * @author mazur_ea
 * Date:   07.07.2021
 */
enum class ProductDeciphermentAttr(val id: Long) : EnumConvertible<Long> {

    PURCHASE_SPECIFICATION_VERSION(1), // Версия ЗС (BOM)
    COMPOSITION(2), // Состав
    INVOICES(3), // Накладные
    PRODUCT_WORK_COST_JUSTIFICATION(4), // обоснование стоимости работ по изготовлению изделия
    PRODUCT_LABOUR_INTENSITY(5), // список расчетов трудоемкости
    HEAD_PL_EC_DEPARTMENT(6), // начальник планово экономического отдела
    CHIEF_TECHNOLOGIST(7), // главный технолог
    HEAD_PRODUCTION(8), // начальник производства
    PRODUCT_SPEC_RESEARCH_JUSTIFICATION(9), // обоснование цен на специальные исследования изделий
    PRODUCT_SPEC_REVIEW_JUSTIFICATION(10), // обоснование цен на специальные проверки изделий
    HEAD_CONSTRUCT_DEPARTMENT(11), // начальник конструкторского отдела
    DIRECTOR_ECO(12), // директор по экономике
    CUSTOMER(13), // заказчик
    PROCEDURE(14), // процедура
    PRICE_DETERMINATION(15), // метод определения цены
    NOTE(16), // примечание
    OKPD(17), // код ОКП/ОКПД2
    TECH_DOC(18), // техническая документация
    START_DATE(19), // начало действия цены
    END_DATE(20), // окончания действия цены
    PRICE_TYPE(21), // вид цены, предложенный поставщиком
    BASIC_PLAN_ECO_INDICATOR(22), // базовый плановый показатель
    ACCOUNTANT(23), // бухгалтер
    PROFITABILITY_FIRST(24), // бухгалтер
    PROFITABILITY_SECOND(25); // бухгалтер

    override fun toValue() = id

    @PersistenceConverter
    class Converter : EnumConverter<ProductDeciphermentAttr, Long>(ProductDeciphermentAttr::class.java)
}