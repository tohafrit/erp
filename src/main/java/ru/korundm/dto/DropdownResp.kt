package ru.korundm.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL

class DropdownResp(
    val results: List<DropdownRespItem> = emptyList(),
    val success: Boolean = true
)

@JsonInclude(NON_NULL)
class DropdownRespItem(
    val name: String = "",
    val value: String = "",
    val text: String? = null,
    val disabled: Boolean? = null,
    val image: String? = null,
    val imageClass: String? = null,
    val icon: String? = null,
    val iconClass: String? = null
)