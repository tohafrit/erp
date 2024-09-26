package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import ru.korundm.entity.FileStorage
import ru.korundm.helper.*
import ru.korundm.repository.FileStorageRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface FileStorageService {

    fun read(id: Long?): FileStorage<*, *>?

    fun <E : FileStorable<E>, T : SingularFileStorableType> readOneSingular(entity: E, type: FileStorageType<E, T>): FileStorage<E, T>?

    fun <E : FileStorable<E>, T : PluralFileStorableType> readOnePlural(entity: E, type: FileStorageType<E, T>): List<FileStorage<E, T>>

    fun <E : FileStorable<E>> readAny(entity: E): List<FileStorage<E, out FileStorableType>>

    fun <E : FileStorable<E>> readAny(entity: E, vararg typeArgs: FileStorageType<E, out FileStorableType>): List<FileStorage<E, out FileStorableType>>

    fun <E : FileStorable<E>> readAny(entity: E, typeList: List<FileStorageType<E, out FileStorableType>>): List<FileStorage<E, out FileStorableType>>

    fun <E : FileStorable<E>> readAny(entityList: List<E>): List<FileStorage<E, out FileStorableType>>

    fun <E : FileStorable<E>> readAny(entityList: List<E>, vararg typeArgs: FileStorageType<E, out FileStorableType>): List<FileStorage<E, out FileStorableType>>

    fun <E : FileStorable<E>> readAny(entityList: List<E>, typeList: List<FileStorageType<E, out FileStorableType>>): List<FileStorage<E, out FileStorableType>>

    fun <E : FileStorable<E>, T : SingularFileStorableType> saveEntityFile(entity: E, type: FileStorageType<E, T>, file: MultipartFile?)

    fun <E : FileStorable<E>, T : PluralFileStorableType> saveEntityFiles(entity: E, type: FileStorageType<E, T>, fileList: List<MultipartFile>, existFileList: List<FileStorage<E, T>> = emptyList()): List<FileStorage<E, T>>

    fun <E : FileStorable<E>> delete(fs: FileStorage<E, out FileStorableType>)
}

/**
 * Сервис для работы с хранимыми файлами [FileStorage]
 * @author mazur_ea
 * Date:   23.02.2021
 */
@Service
@Transactional
class FileStorageServiceImpl(
    private val fileStorageRepository: FileStorageRepository
) : FileStorageService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun read(id: Long?): FileStorage<*, *>? = if (id == null) null else fileStorageRepository.findById(id).orElse(null)

    override fun <E : FileStorable<E>, T : SingularFileStorableType> readOneSingular(entity: E, type: FileStorageType<E, T>): FileStorage<E, T>? {
        val id = entity.storableId()
        return if (id == null) null else fileStorageRepository.findTopByEntityIdAndTypeOrderByIdDesc(id, type)
    }

    override fun <E : FileStorable<E>, T : PluralFileStorableType> readOnePlural(entity: E, type: FileStorageType<E, T>): List<FileStorage<E, T>> {
        val id = entity.storableId()
        return if (id == null) emptyList() else fileStorageRepository.findAllByEntityIdAndType(id, type)
    }

    override fun <E : FileStorable<E>> readAny(entity: E) = readAny(entity, FileStorageType.values(entity.storableClass()))

    override fun <E : FileStorable<E>> readAny(entity: E, vararg typeArgs: FileStorageType<E, out FileStorableType>) = readAny(entity, typeArgs.toList())

    override fun <E : FileStorable<E>> readAny(entity: E, typeList: List<FileStorageType<E, out FileStorableType>>) = readAny(listOf(entity), typeList)

    override fun <E : FileStorable<E>> readAny(entityList: List<E>) = if (entityList.isEmpty()) emptyList() else readAny(entityList, FileStorageType.values(entityList[0].storableClass()))

    override fun <E : FileStorable<E>> readAny(entityList: List<E>, vararg typeArgs: FileStorageType<E, out FileStorableType>) = readAny(entityList, typeArgs.toList())

    /**
     * Метод для получения списка файлов для однотипных сущностей.
     *
     * Если в списке типов атрибутов присутствуют типы единичного хранения [SingularFileStorableType], то запрос выбирает для таких атрибутов
     * последние записи по максимальному id
     *
     * Для множественного типа [PluralFileStorableType] происходит выборка всех файлов
     *
     * Итоговый файловый список отсортирован по возрастанию id
     * @param entityList список однотипных сущностей
     * @param typeList список единичного [SingularFileStorableType] или множественного [PluralFileStorableType] типов хранения
     * @return файловый список, отсортированный в порядке возрастания id
     */
    override fun <E : FileStorable<E>> readAny(entityList: List<E>, typeList: List<FileStorageType<E, out FileStorableType>>): List<FileStorage<E, out FileStorableType>> {
        val entityIdList = entityList.mapNotNull { it.storableId() }
        if (entityIdList.isEmpty() || typeList.isEmpty()) return emptyList()
        val query = """
            SELECT
                fs.id,
                fs.file_system_id,
                fs.entity_id,
                fs.type,
                fs.name,
                fs.create_date
            FROM (
                SELECT
                    ROW_NUMBER() OVER(PARTITION BY fs.entity_id, fs.type ORDER BY fs.id DESC) rn,
                    IF(fs.type IN (:singularTypeIdList), 0, 1) plural,
                    fs.id,
                    fs.file_system_id,
                    fs.entity_id,
                    fs.type,
                    fs.name,
                    fs.create_date
                FROM
                    file_storage fs
                WHERE
                    fs.entity_id in (:entityIdList)
                    AND fs.type in (:typeIdList)
            ) fs
            WHERE IF(fs.plural = 1, fs.rn > 0, fs.rn = 1)
            ORDER BY fs.id
        """.trimIndent()
        val nativeQuery = em.createNativeQuery(query, FileStorage::class.java)
        nativeQuery.setParameter("entityIdList", entityIdList)
        nativeQuery.setParameter("typeIdList", typeList.map { it.id })
        nativeQuery.setParameter("singularTypeIdList", mutableListOf(0).addAll(typeList.filter { it is SingularFileStorableType }.map { it.id }))
        @Suppress("UNCHECKED_CAST")
        return nativeQuery.resultList as List<FileStorage<E, out FileStorableType>>
    }

    /**
     * Метод привязки файла к сущности
     *
     * Выполняет сохранение файла хранения [FileStorage]
     *
     * Удаляет перед сохранением все возможные файлы сущности из БД (хотя для хранения возможен только один файл, исходя из логики)
     * @param entity сущность для привязки
     * @param type тип единичного атрибута хранения [SingularFileStorableType]
     * @param file файл для сохранения в файловую систему. Если null или файл пуст, то сохранения не произойдет, выполнится только часть с удалением
     */
    override fun <E : FileStorable<E>, T : SingularFileStorableType> saveEntityFile(entity: E, type: FileStorageType<E, T>, file: MultipartFile?) {
        readAny(entity, type).forEach { delete(it) }
        if (file != null && !file.isEmpty) save(FileStorage(entity, type, file))
    }

    /**
     * Метод привязки файлов к сущности
     *
     * Выполняет сохранение файла хранения [FileStorage]
     *
     * Перед сохранением удаляет все файлы хранения, id которых не пришли в existFileList параметре
     * @param entity сущность для привязки
     * @param type тип множественного атрибута хранения [PluralFileStorableType]
     * @param fileList список файлов для сохранения в файловую систему
     * @param existFileList список хранимых файлов, которые были привязаны к сущности
     * @return список сохраненных файлов [FileStorage]
     */
    override fun <E : FileStorable<E>, T : PluralFileStorableType> saveEntityFiles(entity: E, type: FileStorageType<E, T>, fileList: List<MultipartFile>, existFileList: List<FileStorage<E, T>>): List<FileStorage<E, T>> {
        readOnePlural(entity, type).filterNot { existFileList.contains(it) }.forEach { delete(it) }
        val list = fileList.filterNot { it.isEmpty }.map { FileStorage(entity, type, it) }.toList()
        list.forEach { save(it) }
        return list
    }

    override fun <E : FileStorable<E>> delete(fs: FileStorage<E, out FileStorableType>) = fileStorageRepository.delete(fs)

    private fun <E : FileStorable<E>, T : FileStorableType> save(fs: FileStorage<E, T>) = fileStorageRepository.save(fs)
}