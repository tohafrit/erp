package ru.korundm.integration.onec.hr.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class Subdivision(
    @JsonProperty("Ref_Key")
    val id: String,
    @JsonProperty("Parent_Key")
    val parent: String,
    @JsonProperty("Description")
    val description: String,
    @JsonProperty("РеквизитДопУпорядочиванияИерархического")
    val sort: String,
    @JsonProperty("Сформировано")
    val formed: Boolean,
    @JsonProperty("Расформировано")
    val disbanded: Boolean
)