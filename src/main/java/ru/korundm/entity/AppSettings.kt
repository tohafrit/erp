package ru.korundm.entity

import ru.korundm.enumeration.AppSettingsAttr
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

@Entity
@Table(name = "app_settings")
data class AppSettings(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Convert(converter = AppSettingsAttr.Converter::class)
    @Column(name = "attr", nullable = false)
    var attr: AppSettingsAttr? = null // атрибут

    @Column(name = "string_val", length = 1024)
    var stringVal: String? = null // строковое значение

    @Column(name = "long_val")
    var longVal: Long? = null // long значение

    @Column(name = "bool_val")
    var boolVal: Boolean? = null // булево значение

    @Column(name = "date_val")
    var dateVal: LocalDate? = null // значение даты

    @Column(name = "datetime_val")
    var datetimeVal: LocalDateTime? = null // значение даты/времени

    @Column(name = "json_val")
    var jsonVal: String? = null // JSON значение

    @Column(name = "decimal_val")
    var decimalVal: BigDecimal? = null // BigDecimal значение
}