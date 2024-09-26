package ru.korundm.enumeration;

import lombok.Getter;
import java.util.Arrays;

public enum BomType {

    LAST_APPROVED(Types.LAST_APPROVED,"bomType.lastApproved"),
    LAST(Types.LAST, "bomType.last"),
    LAUNCH_INDICATED(Types.LAUNCH_INDICATED, "bomType.launchIndicated");

    @Getter
    private long id;

    @Getter
    private String property;

    BomType(long id, String property) {
        this.id = id;
        this.property = property;
    }

    public static BomType getById(long id) {
        return Arrays.stream(values()).filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    public static class Types {

        /** Последние утвержденные */
        public static final long LAST_APPROVED = 0;

        /** Последние */
        public static final long LAST = 1;

        /** Указанные в запуске */
        public static final long LAUNCH_INDICATED = 2;
    }
}