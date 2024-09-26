package ru.korundm.helper

import ru.korundm.entity.FileStorage
import kotlin.reflect.KClass

/**
 * Интерфейс для сущностей, которые связаны с хранимыми файлами загрузки [FileStorage]
 * @author mazur_ea
 * Date:   19.02.2021
 */
interface FileStorable<E : Any> {

    fun storableId(): Long?

    fun storableClass(): KClass<E>
}