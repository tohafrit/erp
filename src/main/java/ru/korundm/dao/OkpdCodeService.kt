package ru.korundm.dao

import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.OkpdCode
import ru.korundm.repository.OkpdCodeRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface OkpdCodeService : CommonService<OkpdCode> {

    fun getLastByCell(cell: String?): OkpdCode?
    fun getAllByType(type: OkpdCode.Type): List<OkpdCode>
    fun existsByType(type: OkpdCode.Type, typeId: Long, id: Long?): Boolean
    fun findAllExistsTypeIdList(type: OkpdCode.Type): List<Long>
    fun getFirstByProductTypeId(productTypeId: Long?): OkpdCode?
    fun existsByProductTypeId(productTypeId: Long?): Boolean
}

@Service
@Transactional
class OkpdCodeServiceImpl(
    private val repository: OkpdCodeRepository
) : OkpdCodeService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<OkpdCode> = repository.findAll()

    override fun getAllById(idList: List<Long>): List<OkpdCode> = repository.findAllById(idList)

    override fun save(obj: OkpdCode): OkpdCode {
        em.detach(obj)
        return repository.save(obj)
    }

    override fun saveAll(objectList: List<OkpdCode>): List<OkpdCode> = repository.saveAll(objectList)

    override fun read(id: Long): OkpdCode? = repository.findById(id).orElse(null)

    override fun delete(obj: OkpdCode) = repository.delete(obj)

    override fun deleteById(id: Long) = repository.deleteById(id)

    override fun getLastByCell(cell: String?) =
        cell?.let { if (StringUtils.isNumeric(it) && it.length == 6) repository.findFirstByComponentGroupNumber(it.substring(0, 2).toInt()) else null }

    override fun getAllByType(type: OkpdCode.Type) = when (type) {
        OkpdCode.Type.PRODUCT -> repository.findAllByProductTypeIsNotNull()
        OkpdCode.Type.COMPONENT -> repository.findAllByComponentGroupIsNotNull()
    }

    override fun existsByType(type: OkpdCode.Type, typeId: Long, id: Long?) = when (type) {
        OkpdCode.Type.PRODUCT -> if (id == null) repository.existsByProductTypeId(typeId) else repository.existsByProductTypeIdAndIdNot(typeId, id)
        OkpdCode.Type.COMPONENT -> if (id == null) repository.existsByComponentGroupId(typeId) else repository.existsByComponentGroupIdAndIdNot(typeId, id)
    }

    override fun findAllExistsTypeIdList(type: OkpdCode.Type) = when (type) {
        OkpdCode.Type.PRODUCT -> repository.findAllExistsProductTypeIdList()
        OkpdCode.Type.COMPONENT -> repository.findAllExistsComponentGroupIdList()
    }

    override fun getFirstByProductTypeId(productTypeId: Long?) = if (productTypeId == null) null else repository.findFirstByProductTypeId(productTypeId)

    override fun existsByProductTypeId(productTypeId: Long?) = if (productTypeId == null) false else repository.existsByProductTypeId(productTypeId)
}