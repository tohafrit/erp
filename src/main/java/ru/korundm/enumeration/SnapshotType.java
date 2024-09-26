package ru.korundm.enumeration;

import lombok.Getter;

import java.util.Arrays;

public enum SnapshotType {

    CURRENT_LAUNCH(Types.CURRENT_LAUNCH,"snapshotType.obligation.currentLaunch"),
    PREVIOUS_LAUNCH(Types.PREVIOUS_LAUNCH,"snapshotType.obligation.previousLaunch"),
    @Deprecated
    MEMORANDUM(Types.MEMORANDUM,"snapshotType.obligation.memorandum"),
    @Deprecated
    STRATEGIC_RESERVE(Types.STRATEGIC_RESERVE,"snapshotType.strategicReserve"),
    STOCK_BALANCE(Types.STOCK_BALANCE,"snapshotType.stockBalance"),
    @Deprecated
    ASU_DEFICIT(Types.ASU_DEFICIT,"snapshotType.asuDeficit"),
    SAFEKEEPING(Types.SAFEKEEPING,"snapshotType.safekeeping"),
    MISSED_COMPONENT(Types.MISSED_COMPONENT, "snapshotType.missedComponent"),
    ONE_C(Types.ONE_C, "snapshotType.oneC");

    @Getter
    private long id;

    @Getter
    private String property;

    SnapshotType(long id, String property) {
        this.id = id;
        this.property = property;
    }

    public static SnapshotType getById(long id) {
        return Arrays.stream(values()).filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    public static class Types {

        /** Обязательства по текущему запуску */
        static final long CURRENT_LAUNCH = 1;

        /** Обязательства по предыдущим запускам */
        static final long PREVIOUS_LAUNCH = 2;

        /** Обязательства по служебным запискам */
        @Deprecated
        static final long MEMORANDUM = 3;

        /** Стратегический резерв */
        @Deprecated
        static final long STRATEGIC_RESERVE = 4;

        /** Остатки на складе */
        static final long STOCK_BALANCE = 5;

        /** Дефицит из АСУ */
        @Deprecated
        static final long ASU_DEFICIT = 6;

        /** Ответственное хранение */
        static final long SAFEKEEPING = 7;

        /** Непринятые компоненты */
        static final long MISSED_COMPONENT = 8;

        /** Товары в пути (из 1С) */
        static final long ONE_C = 9;
    }
}