package ru.korundm.report.excel.enumeration

/**
 * Перечисление для хранения свойств стиля ячейки
 * @author mazur_ea
 * Date:   25.10.2019
 */
enum class CellStylePropertyEntry {

    /** Выравнивание по горизонтали  */
    HORIZONTAL_ALIGNMENT,

    /** Выравнивание по вертикали  */
    VERTICAL_ALIGNMENT,

    /** Верхняя граница  */
    BORDER_TOP,

    /** Правая граница  */
    BORDER_RIGHT,

    /** Нижняя граница  */
    BORDER_BOTTOM,

    /** Левая граница  */
    BORDER_LEFT,

    /** Цвет верхней границы  */
    TOP_BORDER_COLOR,

    /** Цвет правой границы  */
    RIGHT_BORDER_COLOR,

    /** Цвет нижней границы  */
    BOTTOM_BORDER_COLOR,

    /** Цвет левой границы  */
    LEFT_BORDER_COLOR,

    /** Индекс форматора данных  */
    DATA_FORMAT,

    /** Тип заполнения ячейки  */
    FILL_PATTERN,

    /** Цвет ячейки  */
    FILL_FOREGROUND_COLOR,

    /** Цвет бэкгрануда ячейки  */
    FILL_BACKGROUND_COLOR,

    /** Индекс шрифта  */
    FONT_INDEX,

    /** Флаг скрытой ячейки  */
    HIDDEN,

    /** Количество пробелов для отступа текста  */
    INDENTION,

    /** Флаг блокировки ячейки  */
    LOCKED,

    /** Угол поворота текста внутри ячейки  */
    ROTATION,

    /** Флаг обозначающий, что текст ячейки будет обернут (текст не выходит с границы ячейки)  */
    WRAP_TEXT,

    /** Должна ли ячейка автоматически изменять размеры в Excel, чтобы уменьшить ее размер, если этот текст слишком длинный  */
    SHRINK_TO_FIT
}