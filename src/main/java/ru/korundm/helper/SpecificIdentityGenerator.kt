package ru.korundm.helper

import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentityGenerator
import java.io.Serializable

/**
 * Кастомный генератор идентификаторов для сохранения сущностей
 * Используется для того, чтобы вручную устанавливать поле id в сущности
 * @author mazur_ea
 * Date:   23.12.2019
 */
@Suppress("unused")
class SpecificIdentityGenerator : IdentityGenerator() {

    override fun generate(session: SharedSessionContractImplementor, obj: Any): Serializable =
        obj.javaClass.getMethod("getId").invoke(obj) as Long? ?: super.generate(session, obj)
}