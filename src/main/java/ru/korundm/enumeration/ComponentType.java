package ru.korundm.enumeration;

import lombok.Getter;

import java.util.Arrays;

public enum ComponentType {

    COMPONENT(Types.COMPONENT,"componentType.component"),
    NEW_COMPONENT(Types.NEW_COMPONENT, "componentType.newComponent"),
    STUB(Types.STUB, "componentType.stub");

    @Getter
    private final long id;

    @Getter
    private final String property;

    ComponentType(long id, String property) {
        this.id = id;
        this.property = property;
    }

    public static ComponentType getById(long id) {
        return Arrays.stream(values()).filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    public static class Types {

        /** Компонента */
        public static final long COMPONENT = 1;

        /** Новая компонента */
        public static final long NEW_COMPONENT = 2;

        /** Заглушка */
        public static final long STUB = 3;
    }
}