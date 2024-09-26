package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

/**
 * Список типов алгоритмов распределения
 * @author zhestkov_an
 * Date:   20.05.2021
 */
enum class DistributionAlgorithmType(
    val id: Long,
    val property: String,
    val description: String
) : EnumConvertible<Long> {

    PROPORTIONALLY(1, "distributionAlgorithmType.proportionally", "Увеличивать пропорционально"),
    ADD70(2, "distributionAlgorithmType.add70", "Последовательно добавлять до 70%"),
    ADD100(3, "distributionAlgorithmType.add100", "Последовательно добавлять до 100%");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<DistributionAlgorithmType, Long>(DistributionAlgorithmType::class.java)
}