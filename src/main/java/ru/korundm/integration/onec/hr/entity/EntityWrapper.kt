package ru.korundm.integration.onec.hr.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class EntityWrapper<T>(
    @JsonProperty(value = "value")
    val entityList: List<T> = emptyList()
)