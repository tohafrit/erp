package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.Document
import ru.korundm.repository.DocumentRepository

interface DocumentService : CommonService<Document>

@Service
@Transactional
class DocumentServiceImpl(
    private val documentRepository: DocumentRepository
) : DocumentService {

    override fun getAll(): List<Document> = documentRepository.findAll()

    override fun getAllById(idList: List<Long>): List<Document> = documentRepository.findAllById(idList)

    override fun save(obj: Document) = documentRepository.save(obj)

    override fun saveAll(objectList: List<Document>): List<Document> = documentRepository.saveAll(objectList)

    override fun read(id: Long): Document? = documentRepository.findById(id).orElse(null)

    override fun delete(obj: Document) = documentRepository.delete(obj)

    override fun deleteById(id: Long) = documentRepository.deleteById(id)
}