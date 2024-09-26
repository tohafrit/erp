package ru.korundm.integration.onec.hr.dao

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.korundm.integration.onec.Constant.BASE_URI
import ru.korundm.integration.onec.Constant.Catalog.EMPLOYEE
import ru.korundm.integration.onec.Constant.Catalog.SUBDIVISIONS
import ru.korundm.integration.onec.Constant.Query.EXPAND
import ru.korundm.integration.onec.Constant.Query.FORMAT
import ru.korundm.integration.onec.Constant.Query.PARAMS_SEPARATOR
import ru.korundm.integration.onec.Constant.Query.PARAMS_UNITE
import ru.korundm.integration.onec.hr.entity.EntityWrapper
import ru.korundm.integration.onec.hr.entity.Subdivision
import ru.korundm.integration.onec.hr.entity.Employee

@Component
class CommonService(
    private val restTemplate: RestTemplate,
    private val httEntity: HttpEntity<String>
) {
    fun <T> findById(uri: String): T? {
        return restTemplate.exchange(uri, HttpMethod.GET, httEntity, object : ParameterizedTypeReference<T>() {}).body
    }

    fun <T> findEntityList(uri: String): List<T>? {
        return restTemplate.exchange(
            uri, HttpMethod.GET, httEntity, object : ParameterizedTypeReference<EntityWrapper<T>>() {}
        ).body?.entityList
    }
}

@Component
class CommonServiceImpl(
    private val commonService: CommonService,
    private val jsonMapper: ObjectMapper
) {
    fun getUserList(): List<Employee>? {
        val expand = "${EXPAND}ФизическоеЛицо"
        val uri = "${BASE_URI}${EMPLOYEE}${PARAMS_SEPARATOR}${expand}${PARAMS_UNITE}${FORMAT}"
        return jsonMapper.convertValue(
            commonService.findEntityList<List<Employee>?>(uri),
            object : TypeReference<List<Employee>>() {}
        )
    }

    // Подразделения
    fun getSubdivisionList(): List<Subdivision>? {
        val uri = "${BASE_URI}${SUBDIVISIONS}${PARAMS_SEPARATOR}${FORMAT}"
        return jsonMapper.convertValue(
            commonService.findEntityList<List<Subdivision>?>(uri),
            object : TypeReference<List<Subdivision>>() {}
        )
    }

    // Подразделение по идентификатору
    fun getSubdivisionById(id: String): Subdivision? {
        val filter = "(guid'${id}')"
        val uri = "${BASE_URI}${SUBDIVISIONS}${filter}${PARAMS_SEPARATOR}${FORMAT}"
        return commonService.findById(uri)
    }
}