package ru.korundm.integration.onec

import ru.korundm.integration.onec.Constant.NULL

object OnecUtil {

    fun isNull(id: String) = NULL == id

    fun isNotNull(id: String) = !isNull(id)
}