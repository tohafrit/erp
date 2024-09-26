package ru.korundm.helper

/**
 * Перечисление для работы с выдачами вложений в http-ответ
 * @author mazur_ea
 * Date:   07.10.2019
 */
enum class AttachmentMediaType(
    val contentType: String, // тип заголовка контента
    val extension: String // расширение
) {
    XLS("application/vnd.ms-excel", "xls"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
    DOC("application/msword", "doc"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
    PDF("application/pdf", "pdf"),
    ZIP("application/zip", "zip"),
    XML("application/xml", "xml");
}