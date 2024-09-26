package ru.korundm.integration.onec.hr.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class Organization (
    @JsonProperty("Ref_Key")
    val id: String,
    @JsonProperty("Description")
    val fullName: String,
    @JsonProperty("ВАрхиве")
    val active: Boolean
)