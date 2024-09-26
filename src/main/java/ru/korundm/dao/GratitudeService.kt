package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.Gratitude
import ru.korundm.repository.GratitudeRepository

interface GratitudeService : CommonService<Gratitude>

@Service
@Transactional
class GratitudeServiceImpl(
    private val gratitudeRepository: GratitudeRepository
) : GratitudeService {

    override fun getAll(): List<Gratitude> = gratitudeRepository.findAll()

    override fun getAllById(idList: List<Long>): List<Gratitude> = gratitudeRepository.findAllById(idList)

    override fun save(obj: Gratitude) = gratitudeRepository.save(obj)

    override fun saveAll(objectList: List<Gratitude>): List<Gratitude> = gratitudeRepository.saveAll(objectList)

    override fun read(id: Long): Gratitude? = gratitudeRepository.findById(id).orElse(null)

    override fun delete(obj: Gratitude) = gratitudeRepository.delete(obj)

    override fun deleteById(id: Long) = gratitudeRepository.deleteById(id)
}