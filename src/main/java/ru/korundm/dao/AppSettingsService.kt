package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.AppSettings
import ru.korundm.enumeration.AppSettingsAttr
import ru.korundm.repository.AppSettingsRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface AppSettingsService : CommonService<AppSettings> {

    fun findFirstByAttr(attr: AppSettingsAttr?): AppSettings?
}

@Service
@Transactional
class AppSettingsServiceImpl(
    private val repository: AppSettingsRepository
) : AppSettingsService {

    private val cl = AppSettings::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<AppSettings> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<AppSettings> = repository.findAllById(idList)

    override fun save(obj: AppSettings): AppSettings {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<AppSettings>): List<AppSettings> = repository.saveAll(objectList)

    override fun read(id: Long): AppSettings? = repository.findById(id).orElse(null)

    override fun delete(obj: AppSettings) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun findFirstByAttr(attr: AppSettingsAttr?) = if (attr == null) null else repository.findFirstByAttr(attr)
}