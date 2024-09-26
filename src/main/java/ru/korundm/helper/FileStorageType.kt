package ru.korundm.helper

import ru.korundm.entity.*
import javax.persistence.AttributeConverter
import kotlin.reflect.KClass
import javax.persistence.Converter as PersistenceConverter

/**
 * Типы хранимых файлов загрузки [FileStorage]
 * @author mazur_ea
 * Date:   19.02.2021
 */
sealed class FileStorageType<E : FileStorable<E>, T : FileStorableType>(
    val id: Int,
    private val cl: KClass<E>
) {

    object GratitudeParamFile : FileStorageType<Gratitude, SingularFileStorableType>(1, Gratitude::class)
    object NewsParamFile : FileStorageType<News, SingularFileStorableType>(2, News::class)
    object DeciphermentRawMaterialFile : FileStorageType<ProductDecipherment, PluralFileStorableType>(3, ProductDecipherment::class)
    object DeciphermentPurchasedComponentFile : FileStorageType<ProductDecipherment, PluralFileStorableType>(4, ProductDecipherment::class)
    object DeciphermentTareAndPackagingFile : FileStorageType<ProductDecipherment, PluralFileStorableType>(5, ProductDecipherment::class)
    object ProductDocumentationFile : FileStorageType<ProductDocumentation, SingularFileStorableType>(6, ProductDocumentation::class)
    object ContractSectionDocumentationFile : FileStorageType<ContractSectionDocumentation, SingularFileStorableType>(7, ContractSectionDocumentation::class)
    object ProductWorkCostJustificationFile : FileStorageType<ProductWorkCostJustification, SingularFileStorableType>(8, ProductWorkCostJustification::class)
    object ProductSpecReviewJustificationFile : FileStorageType<ProductSpecReviewJustification, SingularFileStorableType>(9, ProductSpecReviewJustification::class)
    object ProductSpecResearchJustificationFile : FileStorageType<ProductSpecResearchJustification, SingularFileStorableType>(10, ProductSpecResearchJustification::class)
    object ProductDeciphermentFile : FileStorageType<ProductDecipherment, SingularFileStorableType>(11, ProductDecipherment::class)

    @PersistenceConverter
    class Converter : AttributeConverter<FileStorageType<*, *>, Int> {

        override fun convertToDatabaseColumn(type: FileStorageType<*, *>) = type.id

        override fun convertToEntityAttribute(value: Int) = values().firstOrNull { it.id == value }
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun <E : FileStorable<E>> values(cl: KClass<E>) = values().filter { it.cl == cl } as List<FileStorageType<E, out FileStorableType>>

        private fun values() = FileStorageType::class.sealedSubclasses.mapNotNull { it.objectInstance }
    }
}