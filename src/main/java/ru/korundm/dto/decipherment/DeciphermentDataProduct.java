package ru.korundm.dto.decipherment;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для хранения модели данных для работы с иерархией изделий при формировании расшифровки
 * @author mazur_ea
 * Date:   29.08.2019
 */
public final class DeciphermentDataProduct {

    // Параметры для построения иерархии изделий
    private int rowCount = 1; // для подсчета номера строки в рекурсивной итерации при построении excel-отчета

    private DeciphermentDataProduct parentProduct; // ссылка на родительский продукт в составе (на текущий момент требуется для формирования номера строки)
    private List<DeciphermentDataProduct> subProductList = new ArrayList<>(); // список подизделий

    // Параметры изделия
    private String uniqueNumber; // уникальный номер
    private String uniqueParentNumber; // уникальный номер предка
    private String name; // наименование
    private Long productCount; // количество
    private List<DeciphermentDataComponent> componentList = new ArrayList<>(); // компоненты изделия

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public DeciphermentDataProduct getParentProduct() {
        return parentProduct;
    }

    public void setParentProduct(DeciphermentDataProduct parentProduct) {
        this.parentProduct = parentProduct;
    }

    public List<DeciphermentDataProduct> getSubProductList() {
        return subProductList;
    }

    public void setSubProductList(List<DeciphermentDataProduct> subProductList) {
        this.subProductList = subProductList;
    }

    public String getUniqueNumber() {
        return uniqueNumber;
    }

    public void setUniqueNumber(String uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }

    public String getUniqueParentNumber() {
        return uniqueParentNumber;
    }

    public void setUniqueParentNumber(String uniqueParentNumber) {
        this.uniqueParentNumber = uniqueParentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProductCount() {
        return productCount;
    }

    public void setProductCount(Long productCount) {
        this.productCount = productCount;
    }

    public List<DeciphermentDataComponent> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<DeciphermentDataComponent> componentList) {
        this.componentList = componentList;
    }
}