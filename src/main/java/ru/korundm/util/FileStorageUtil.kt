package ru.korundm.util

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils.deleteDirectory
import org.apache.commons.io.FilenameUtils
import org.springframework.http.HttpHeaders
import org.springframework.util.FileCopyUtils
import org.springframework.web.multipart.MultipartFile
import ru.korundm.constant.BaseConstant.FILE_SIZE_LIMIT
import ru.korundm.constant.ValidatorMsg.EXCEED_FILE_SIZE_LIMIT
import ru.korundm.constant.ValidatorMsg.FILES_REQUIRED
import ru.korundm.entity.FileStorage
import ru.korundm.entity.FileSystemStorage
import ru.korundm.helper.*
import ru.korundm.helper.FileSystemStorageContentType.DOCUMENT
import ru.korundm.helper.FileSystemStorageContentType.IMAGE
import ru.korundm.util.EncryptionUtil.decrypt
import ru.korundm.util.EncryptionUtil.encrypt
import java.io.File
import java.io.FileInputStream
import java.net.URLConnection
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Files.createDirectories
import java.nio.file.Paths
import java.util.*
import javax.mail.internet.MimeUtility
import javax.servlet.http.HttpServletResponse

/**
 * Утилити класс для работы с файлами загрузки
 * @author mazur_ea
 * Date:   23.01.2019
 */
object FileStorageUtil {

    private const val ENC_URL_BASE64_KEY = "ZmlsZVN0b3JhZ2VFbmNyeXB0S2V5"
    private val SEP = File.separator
    private val UPLOAD_PATH = "${System.getProperty("jboss.server.base.dir")}${SEP}upload$SEP"
    private val IMAGE_EXTENSIONS = listOf("jpg", "bmp", "jpeg", "jpe", "gif", "png")

    /**
     * Метод проверки файла [MultipartFile] на принадлежность к файлам-изображениям
     * @param file файл
     * @return true, если файл - изображение, иначе false
     */
    private fun isImage(file: MultipartFile) = IMAGE_EXTENSIONS.contains(FilenameUtils.getExtension(file.originalFilename ?: file.name))

    /**
     * Метод определяет тип контента [FileSystemStorageContentType] исходя из файла загрузки [MultipartFile]
     * @param file файл
     * @return тип контента файла
     */
    fun contentType(file: MultipartFile) = if (isImage(file)) IMAGE else DOCUMENT

    /**
     * Метод записи файла в файловую систему хранилища
     * @param fss файл в файловой системе хранилища
     */
    fun write(fss: FileSystemStorage) {
        val fs = fss.fileStorage
        val file = fs.file
        val strPath = "$UPLOAD_PATH${fss.type.directory}$SEP${hash(fss)}"
        val path = Paths.get(strPath)
        if (Files.exists(path)) deleteDirectory(path.toFile())
        createDirectories(path)
        file.transferTo(Paths.get("$path$SEP${fs.name}"))
    }

    /**
     * Метод загрузки файла из хранилища и выдачи его пользователю
     * @param response ответ
     * @param fs файл-сущность
     */
    fun download(response: HttpServletResponse, fs: FileStorage<*, *>) {
        val name = fs.name
        val file = file(fs)
        response.contentType = URLConnection.guessContentTypeFromName(name)
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${MimeUtility.encodeText(name, StandardCharsets.UTF_8.displayName(), "Q")}\"")
        response.setContentLength(file.length().toInt())
        FileCopyUtils.copy(FileInputStream(file), response.outputStream)
    }

    /**
     * Метод получения сохраненного файла [File] по файлу хранения [FileStorage]
     * @param fs файл хранения [FileStorage]
     * @return сохраненный файл [File]
     */
    fun file(fs: FileStorage<*, *>): File {
        val name = fs.name
        val fileSystem = fs.fileSystem!!
        return Paths.get("$UPLOAD_PATH${fileSystem.type.directory}$SEP${hash(fileSystem)}$SEP${name}").toFile()
    }

    /**
     * Метод удаления файла из файловой системы хранилища
     * @param fss файл в файловой системе хранилища
     */
    fun delete(fss: FileSystemStorage) {
        val file = Paths.get("$UPLOAD_PATH${fss.type.directory}$SEP${hash(fss)}").toFile()
        if (file.exists()) deleteDirectory(file)
    }

    /**
     * Метод формирования хэша URL строки по идентификатору файла хранения [FileStorage]
     * @param id идентификатор файла хранения [FileStorage]
     * @return хэш URL строки
     */
    fun encodeURLHash(id: Long?): String =
        id?.let { Base64.getEncoder().encodeToString(encrypt(it.toString(), ENC_URL_BASE64_KEY).toByteArray()) } ?: ""

    /**
     * Метод получения идентификатора файла харнения [FileStorage] из хэша URL строки
     * @param hash хэш URL строки
     * @return идентификатор файла хранения [FileStorage]
     */
    fun decodeURLHash(hash: String) = try {
        decrypt(String(Base64.getDecoder().decode(hash)), ENC_URL_BASE64_KEY).toLong()
    } catch (e: Exception) { null }

    /**
     * Метод получения хэша для файла в файловой системе хранилища [FileSystemStorage]
     * @param fss файл в файловой системе хранилища
     * @return строка с хэшем для файла в файловой системе хранилища
     */
    private fun hash(fss: FileSystemStorage?) = fss?.id?.let { DigestUtils.md5Hex(it.toString()) } ?: throw IllegalArgumentException("empty FileSystemStorage")

    /**
     * Метод получения списка [FileStorage] по типу одиночной связи хранения [FileStorageType]
     * @param entity сущность [FileStorable]
     * @param type тип [FileStorageType]
     * @return [FileStorage]
     */
    fun <E : FileStorable<E>, T : SingularFileStorableType> List<FileStorage<E, out FileStorableType>>.extractSingular(entity: E, type: FileStorageType<E, T>) =
        this.lastOrNull { it.entityId == entity.storableId() && it.type == type }

    /**
     * Метод получения списка [FileStorage] по типу множественной связи хранения [FileStorageType]
     * @param entity сущность [FileStorable]
     * @param type тип [FileStorageType]
     * @return список [FileStorage]
     */
    fun <E : FileStorable<E>, T : PluralFileStorableType> List<FileStorage<E, out FileStorableType>>.extractPlural(entity: E, type: FileStorageType<E, T>) =
        this.filter { it.entityId == entity.storableId() && it.type == type }.toList()

    /**
     * Метод валидации файла формы
     *
     * Если размер файла привышен максимально допустимый размер в системе, также будет выдана ошибка
     * @param errors объект ошибок [ValidatorErrors]
     * @param existFile хранимый файл
     * @param file прикрепленный файл [MultipartFile]
     * @param field поле формы
     * @param required true - поле с файлом должно быть не пустым, иначе - false
     * @param msg сообщение при ошибке
     */
    @JvmOverloads
    fun validateFile(errors: ValidatorErrors, existFile: FileStorage<*, *>?, file: MultipartFile?, field: String, required: Boolean = false, msg: String = FILES_REQUIRED) {
        if (required && (existFile?.id == null) && (file == null || file.isEmpty)) errors.putError(field, msg)
        file?.run { if (size > FILE_SIZE_LIMIT) errors.putError(field, EXCEED_FILE_SIZE_LIMIT, FILE_SIZE_LIMIT) }
    }

    /**
     * Метод валидации файлов формы
     *
     * Если размер файла привышен максимально допустимый размер в системе, также будет выдана ошибка
     * @param errors объект ошибок [ValidatorErrors]
     * @param existFileList список хранимых файлов [FileStorage]
     * @param files список прикрепленных файлов [MultipartFile]
     * @param required true - к полю должен быть прикреплен хотя бы один файл, иначе - false
     * @param msg сообщение при ошибке
     */
    @JvmOverloads
    fun validateFiles(errors: ValidatorErrors, existFileList: List<FileStorage<*, *>>?, files: Array<MultipartFile?>?, field: String, required: Boolean = false, msg: String = FILES_REQUIRED) {
        if (required && existFileList?.none { it.id != null } != false && files?.none { it != null && !it.isEmpty } != false) errors.putError(field, msg)
        files?.run st@{ this.forEach { it?.run { if (size > FILE_SIZE_LIMIT) {
            errors.putError(field, EXCEED_FILE_SIZE_LIMIT, FILE_SIZE_LIMIT)
            return@st
        } } } }
    }
}