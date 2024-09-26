package ru.korundm.report.excel.document.military;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Acceptance implements Serializable {

    private String contractName; // реквизиты договора
    private LocalDate deliveryDate; // дата поставки
    private List<ProductInfo> productInfoList = new ArrayList<>(); // список информации об изделиях

    @Getter @Setter
    public static class ProductInfo {

        private String productName; // название изделия
        private String protocolNumber; // номер протокола
        private BigDecimal protocolPrice; // цена
        private LocalDate protocolDate; // дата протокола
        private String protocolNote; // заключение
        private String type; // тип
    }
}