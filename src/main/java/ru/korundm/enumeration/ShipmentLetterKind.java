package ru.korundm.enumeration;

import lombok.Getter;

import java.util.Arrays;

public enum ShipmentLetterKind {

    LETTER(Types.LETTER, "shipmentLetterKind.letter", "lkdLETTER"),
    OFFICIAL_MEMO(Types.OFFICIAL_MEMO, "shipmentLetterKind.officialMemo", "lkdOFFICE_MEMO"),
    OFFICIAL_MEMO_OKR(Types.OFFICIAL_MEMO_OKR, "shipmentLetterKind.officialMemoOKR", "lkdOFFICE_MEMO_OKR");

    @Getter
    private long id;

    @Getter
    private String property;

    @Getter
    private String code;

    ShipmentLetterKind(long id, String property, String code) {
        this.id = id;
        this.property = property;
        this.code = code;
    }

    public static ShipmentLetterKind getById(long id) {
        return Arrays.stream(values()).filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    public static class Types {

        /** Письмо */
        static final long LETTER = 1;

        /** Служебная записка */
        static final long OFFICIAL_MEMO = 2;

        /** Служебная записка ОКР */
        static final long OFFICIAL_MEMO_OKR = 4;

    }
}