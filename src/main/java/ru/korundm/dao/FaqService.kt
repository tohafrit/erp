package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.Faq
import ru.korundm.repository.FaqRepository

interface FaqService : CommonService<Faq>

@Service
@Transactional
class FaqServiceImpl(
    private val faqRepository: FaqRepository
) : FaqService {

    override fun getAll(): List<Faq> = faqRepository.findAllByIdIsNotNullOrderBySortDescIdAsc()

    override fun getAllById(idList: List<Long>): List<Faq> = faqRepository.findAllById(idList)

    override fun save(obj: Faq) = faqRepository.save(obj)

    override fun saveAll(objectList: List<Faq>): List<Faq> = faqRepository.saveAll(objectList)

    override fun read(id: Long): Faq? = faqRepository.findById(id).orElse(null)

    override fun delete(obj: Faq) = faqRepository.delete(obj)

    override fun deleteById(id: Long) = faqRepository.deleteById(id)
}