package ru.korundm.entity

import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import javax.persistence.*

/**
 * Сущность с описанием компаний/организаций (таблица companies)
 * @author zhestkov_an
 * Date:   03.03.2021
 */
@Entity
@Table(name = "companies")
data class Company(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "company")
    @GenericGenerator(name = "company", strategy = BaseConstant.GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Column(name = "name", length = 128, nullable = false)
    var name = "" // наименование

    @Column(name = "short_name", length = 256)
    var shortName: String? = null // короткое наименование

    @Column(name = "full_name", length = 256)
    var fullName: String? = null // полное наименование

    @Column(name = "chief_name", length = 128)
    var chiefName: String? = null // ФИО руководителя

    @Column(name = "chief_position", length = 256)
    var chiefPosition: String? = null // должность руководителя

    @Column(name = "phone_number", length = 256)
    var phoneNumber: String? = null // телефон/факс

    @Column(name = "contact_person", length = 256)
    var contactPerson: String? = null // контактные лица

    @Column(name = "inn", length = 16)
    var inn: String? = null // ИНН

    @Column(name = "kpp", length = 16)
    var kpp: String? = null // КПП

    @Column(name = "ogrn", length = 16)
    var ogrn: String? = null // ОГРН

    @Column(name = "inspector_name", length = 128)
    var inspectorName: String? = null // название ПЗ

    @Column(name = "inspector_head", length = 128)
    var inspectorHead: String? = null // руководитель ПЗ

    @Column(name = "location", length = 128)
    var location: String? = null // местонахождение

    @Column(name = "note", length = 1024)
    var note: String? = null // комментарий

    @Column(name = "factual_address", length = 256)
    var factualAddress: String? = null // фактический адрес

    @Column(name = "juridical_address", length = 256)
    var juridicalAddress: String? = null // юридический адрес

    @Column(name = "mail_address", length = 256)
    var mailAddress: String? = null // почтовый адрес

    @OneToMany(mappedBy = "company", cascade = [CascadeType.ALL], orphanRemoval = true)
    var companyTypeList = mutableListOf<CompanyType>()

    @OneToMany(mappedBy = "customer")
    var contractList = mutableListOf<Contract>()

    @OneToMany(mappedBy = "company")
    var labourProtocolList = mutableListOf<LabourProtocol>()

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}

@Suppress("unused")
object CompanyM {
    const val ID = "id"
    const val NAME = "name"
    const val SHORT_NAME = "shortName"
    const val FULL_NAME = "fullName"
    const val CHIEF_NAME = "chiefName"
    const val CHIEF_POSITION = "chiefPosition"
    const val PHONE_NUMBER = "phoneNumber"
    const val CONTACT_PERSON = "contactPerson"
    const val INN = "inn"
    const val KPP = "kpp"
    const val OGRN = "ogrn"
    const val INSPECTOR_NAME = "inspectorName"
    const val INSPECTOR_HEAD = "inspectorHead"
    const val LOCATION = "location"
    const val NOTE = "note"
    const val FACTUAL_ADDRESS = "factualAddress"
    const val JURIDICAL_ADDRESS = "juridicalAddress"
    const val MAIL_ADDRESS = "mailAddress"
    const val COMPANY_TYPE_LIST = "companyTypeList"
    const val CONTRACT_LIST = "contractList"
    const val IN_OUT_DOCUMENT_LIST_BY_RECEIVER = "inOutDocumentListByReceiver"
    const val IN_OUT_DOCUMENT_LIST_BY_OUT_CORRESP = "inOutDocumentListByOutCorresp"
    const val LABOUR_PROTOCOL_LIST = "labourProtocolList"
    const val GRATITUDE_LIST = "gratitudeList"
}