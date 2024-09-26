package ru.korundm.helper

import ru.korundm.entity.FileStorage

/**
 * Интерфейс-маркер для определения типов связи с хранимыми файлами загрузки [FileStorage]
 * @author mazur_ea
 * Date:   19.02.2021
 */
interface FileStorableType
interface SingularFileStorableType : FileStorableType
interface PluralFileStorableType : FileStorableType