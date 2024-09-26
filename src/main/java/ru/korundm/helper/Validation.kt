package ru.korundm.helper

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ru.korundm.helper.AutowireHelper.autowire
import javax.annotation.Resource
import javax.validation.ConstraintViolation

/**
 * Интерфейс для реализации валидации
 * @author mazur_ea
 * Date:   02.04.2020
 */
interface Validatable {
    fun validate(errors: ValidatorErrors)
}

/**
 * Класс для реализации ошибок валидации
 * @author mazur_ea
 * Date:   02.04.2020
 */
class ValidatorErrors(private val response: ValidatorResponse) {

    /**
     * Добавление ошибки
     * @param field поле
     * @param messageKey ключ сообщения в ресурсах
     * @param args параметры сообщения
     */
    fun putError(field: String, messageKey: String, vararg args: Any) = response.putError(field, messageKey, *args)

    /**
     * Добавление ошибки в ответ
     * @param field поле
     * @param message сообщение
     */
    fun putStringError(field: String, message: String) = response.putStringError(field, message)
}

/**
 * Класс для реализации ответа валидации
 * @author mazur_ea
 * Date:   02.04.2020
 */
class ValidatorResponse() {

    private val locale = LocaleContextHolder.getLocale()

    init { autowire(this) }

    constructor(validatable: Validatable) : this() {
        fill(validatable)
    }

    /** Сслыка на ресурс для получения сообщений  */
    @Resource
    private lateinit var messageSource: MessageSource

    /** Словарь ошибок  */
    val errors = hashMapOf<String, String>()

    /** Словарь атрибутов данных ответа  */
    val attributes = hashMapOf<String, Any>()

    /**
     * Успешна ли валидация
     */
    val isValid: Boolean
        get() = errors.isEmpty()

    /**
     * Ошибочна ли валидация
     */
    val isError: Boolean
        get() = !isValid

    /**
     * Добавление ошибки в ответ
     * @param field поле
     * @param messageKey ключ сообщения в ресурсах
     * @param args параметры сообщения
     */
    fun putError(field: String, messageKey: String, vararg args: Any) = errors.put(field, messageSource.getMessage(messageKey, args, locale))

    /**
     * Добавление ошибки в ответ
     * @param field поле
     * @param message сообщение
     */
    fun putStringError(field: String, message: String) = errors.put(field, message)

    /**
     * Добавление атрибута в ответ
     * @param field поле
     * @param value объект-значение
     */
    fun putAttribute(field: String, value: Any?) = value?.let { attributes[field] = it }

    /**
     * Заполнение ошибок для результата hibernate-валидации
     * @param cvs результат hibernate-валидации
     */
    fun fill(cvs: Set<ConstraintViolation<Any>>) = cvs.forEach { errors[it.propertyPath.toString()] = it.message }

    /**
     * Выполнение валидацию, заполняя ответ
     * @param validatable реализация валидатора [Validatable]
     */
    fun fill(validatable: Validatable) = validatable.validate(ValidatorErrors(this))
}