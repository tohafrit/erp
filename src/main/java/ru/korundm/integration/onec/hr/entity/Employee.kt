package ru.korundm.integration.onec.hr.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class Employee (
    @JsonProperty("Ref_Key")
    val id: String,
    @JsonProperty("Code")
    val code: String,
    @JsonProperty("ФизическоеЛицо")
    val individual: Individual,
    @JsonProperty("ВАрхиве")
    val active: Boolean
)