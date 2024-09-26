package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.NewsType
import ru.korundm.repository.NewsTypeRepository

interface NewsTypeService : CommonService<NewsType>

@Service
@Transactional
class NewsTypeServiceImpl(
    private val newsTypeRepository: NewsTypeRepository
) : NewsTypeService {

    override fun getAll(): List<NewsType> = newsTypeRepository.findAll()

    override fun getAllById(idList: List<Long>): List<NewsType> = newsTypeRepository.findAllById(idList)

    override fun save(obj: NewsType) = newsTypeRepository.save(obj)

    override fun saveAll(objectList: List<NewsType>): List<NewsType> = newsTypeRepository.saveAll(objectList)

    override fun read(id: Long): NewsType? = newsTypeRepository.findById(id).orElse(null)

    override fun delete(obj: NewsType) = newsTypeRepository.delete(obj)

    override fun deleteById(id: Long) = newsTypeRepository.deleteById(id)
}