package ru.korundm.enumeration;

/**
 * Рабочий статус сотрудника
 * @author surov_pv
 * Date:   11.04.2018
 */
public enum EmployeeStatusState {

    WORKING(1, "работает"),
    DISMISSED(2, "уволен");

    private final int value;
    private final String description;

    EmployeeStatusState(int value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * Метод для получения числового представления о статусе сотрудника
     * @return числовое представление
     */
    public int getValue() {
        return value;
    }

    /**
     * Метод для получения текстового представления о статусе сотрудника
     * @return текстовое представление
     */
    public String getDescription() {
        return description;
    }

    /**
     * Метод для получения текстового представления статуса сотрудника по его значению
     * @param value значение
     * @return название статуса сотрудника
     */
    public static String getDescriptionByValue(int value) {
        String description = "";
        for (EmployeeStatusState employeeStatusState : values()) {
            if (employeeStatusState.value == value) {
                description = employeeStatusState.description;
                break;
            }
        }
        return description;
    }
}