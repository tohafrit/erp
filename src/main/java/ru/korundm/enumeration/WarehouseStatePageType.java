package ru.korundm.enumeration;

import lombok.Getter;

public enum WarehouseStatePageType {

    REMAINS("Остатки на складе"),
    SHIPPED("Отгруженные");

    @Getter
    private String property;

    WarehouseStatePageType(String property) {
        this.property = property;
    }
}