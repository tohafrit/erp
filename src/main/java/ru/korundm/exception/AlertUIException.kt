package ru.korundm.exception

/**
 * Класс исключения оповещения о некорректных данных и их обработки
 * @author mazur_ea
 * Date:   13.06.2020
 */
class AlertUIException @JvmOverloads constructor(override val message: String = "", val title: String = "") : RuntimeException(message)