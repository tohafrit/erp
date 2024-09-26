package ru.korundm.configuration

import org.hibernate.dialect.MySQL5Dialect
import org.hibernate.dialect.function.SQLFunctionTemplate
import org.hibernate.type.StandardBasicTypes.*
import ru.korundm.configuration.SQLDialect.Function.COUNT_INT
import ru.korundm.configuration.SQLDialect.Function.COUNT_LONG
import ru.korundm.configuration.SQLDialect.Function.COUNT_OVER
import ru.korundm.configuration.SQLDialect.Function.GROUP_CONCAT
import ru.korundm.configuration.SQLDialect.Function.IFNULL_LONG
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER

/**
 * Класс расширения SQL диалекта для использования нестандартных конструкций
 * Date:   08.09.2021
 */
class SQLDialect : MySQL5Dialect() {

    object Function {
        const val COUNT_OVER = "countOver"
        const val IFNULL_LONG = "ifNullLong"
        const val COUNT_LONG = "countLong"
        const val COUNT_INT = "countInt"
        const val GROUP_CONCAT = "groupConcat"
    }

    init {
        registerFunction(COUNT_OVER, SQLFunctionTemplate(LONG, PART_QUERY_COUNT_OVER))
        registerFunction(IFNULL_LONG, SQLFunctionTemplate(LONG, "IFNULL(?1, ?2)"))
        registerFunction(COUNT_LONG, SQLFunctionTemplate(LONG, "COUNT(?1)"))
        registerFunction(COUNT_INT, SQLFunctionTemplate(INTEGER, "COUNT(?1)"))
        registerFunction(GROUP_CONCAT, SQLFunctionTemplate(STRING, "GROUP_CONCAT(?1 SEPARATOR ?2)"))
    }
}