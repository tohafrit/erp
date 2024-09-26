package ru.korundm.dto.decipherment;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Класс для хранения модели данных накладной для расшифровки по составу изделий
 * @author mazur_ea
 * Date:   01.10.2019
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public final class DeciphermentDataInvoice implements Serializable {

    private Long id; // идентификатор InvoiceString
    private String name; // наименование
    private LocalDate date; // дата
    private String contractNumber; // номер договора/счета
    private String supplierName; // наименование поставщика
    private double price; // цена
    private double initialQuantity; // начальное кол-во
    private double currentQuantity; // текущее кол-во
    private double reservedQuantity; // зарезервированное кол-во
    private double wastedQuantity; // брак
    private double notAcceptedQuantity; // кол-во не принятых
}