package ru.korundm.dto.decipherment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Класс для хранения модели данных накладной по компоненту
 * @author mazur_ea
 * Date:   01.10.2019
 */

public final class DeciphermentDataInvoiceComponent implements Serializable {

    private Long componentId; // идентификатор компонента
    private Long invoiceId; // идентификатор InvoiceString
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String name; // наименование

    private String fileName; // имя файла
    private String filePath; // путь к файлу
    private String fileHash; // хеш файла

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate date; // дата
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double price; // цена
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String supplier; // поставщик
    @JsonIgnore
    private String unitMeasure; // единица измерения
    @JsonIgnore
    private String inn; // ИНН

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getUnitMeasure() {
        return unitMeasure;
    }

    public void setUnitMeasure(String unitMeasure) {
        this.unitMeasure = unitMeasure;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }
}