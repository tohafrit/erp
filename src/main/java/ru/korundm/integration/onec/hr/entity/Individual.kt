package ru.korundm.integration.onec.hr.entity

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class Individual(
    @JsonProperty("Фамилия")
    val surname: String,
    @JsonProperty("Имя")
    val name: String,
    @JsonProperty("Отчество")
    val patronymic: String,
    @JsonProperty("ДатаРождения")
    val birthday: LocalDate
)