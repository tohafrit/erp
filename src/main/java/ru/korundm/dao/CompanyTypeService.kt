package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.Company
import ru.korundm.entity.CompanyType
import ru.korundm.enumeration.CompanyTypeEnum
import ru.korundm.form.CompanyListFilterForm
import ru.korundm.repository.CompanyTypeRepository

interface CompanyTypeService : CommonService<CompanyType> {

    fun getFirstByCompanyType(type: CompanyTypeEnum): CompanyType

}

@Service
@Transactional
class CompanyTypeServiceImpl(
    private val companyTypeRepository: CompanyTypeRepository
) : CompanyTypeService {

    override fun getAll(): List<CompanyType> = companyTypeRepository.findAll()

    override fun getAllById(idList: List<Long>): List<CompanyType> = companyTypeRepository.findAllById(idList)

    override fun save(obj: CompanyType) = companyTypeRepository.save(obj)

    override fun saveAll(objectList: List<CompanyType>): List<CompanyType> = companyTypeRepository.saveAll(objectList)

    override fun read(id: Long): CompanyType? = companyTypeRepository.findById(id).orElse(null)

    override fun delete(obj: CompanyType) = companyTypeRepository.delete(obj)

    override fun deleteById(id: Long) = companyTypeRepository.deleteById(id)

    override fun getFirstByCompanyType(type: CompanyTypeEnum) = companyTypeRepository.findFirstByType(type)
}