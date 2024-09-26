package ru.korundm.enumeration;

import lombok.Getter;

import java.util.Arrays;

public enum WaterType {

    TRUNK(Types.TRUNK,"waterType.trunk"),
    DI(Types.DI,"waterType.di");

    @Getter
    private String type;

    @Getter
    private String property;

    WaterType(String type, String property) {
        this.type = type;
        this.property = property;
    }

    public static WaterType getByType(String type) {
        return Arrays.stream(values()).filter(item -> item.getType().equals(type)).findFirst().orElse(null);
    }

    public static class Types {

        /** Магистральная */
        static final String TRUNK = "trunk";

        /** DI */
        static final String DI = "di";
    }
}