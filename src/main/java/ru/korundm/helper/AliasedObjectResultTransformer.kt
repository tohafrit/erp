package ru.korundm.helper

import org.hibernate.HibernateException
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl
import org.hibernate.property.access.internal.PropertyAccessStrategyChainedImpl
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl
import org.hibernate.property.access.spi.Setter
import org.hibernate.transform.AliasedTupleSubsetResultTransformer
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Hibernate преобразователь результатов запроса в любой указанный класс
 * @author mazur_ea
 * Date:   03.04.2021
 */
class AliasedObjectResultTransformer<T : Any>(val resultClass: KClass<T>) : AliasedTupleSubsetResultTransformer() {

    var isInitialized = false
    var aliases = emptyArray<String?>()
    var setters = emptyArray<Setter?>()

    override fun isTransformedValueATupleElement(aliases: Array<String>, tupleLength: Int) = false

    override fun transformTuple(tuple: Array<Any>, aliases: Array<String>): Any {
        return try {
            if (this.isInitialized) this.check(aliases) else this.initialize(aliases)
            val result = resultClass.createInstance()
            for (i in aliases.indices) this.setters[i]?.set(result, tuple[i], null)
            result
        } catch (e: InstantiationException) {
            throw HibernateException("Could not instantiate resultclass: ${resultClass.simpleName}")
        } catch (e: IllegalAccessException) {
            throw HibernateException("Could not instantiate resultclass: ${resultClass.simpleName}")
        }
    }

    fun initialize(aliases: Array<String>) {
        val propertyAccessStrategy = PropertyAccessStrategyChainedImpl(
            PropertyAccessStrategyBasicImpl.INSTANCE,
            PropertyAccessStrategyFieldImpl.INSTANCE,
            PropertyAccessStrategyMapImpl.INSTANCE
        )
        this.aliases = arrayOfNulls(aliases.size)
        this.setters = arrayOfNulls(aliases.size)
        for (i in aliases.indices) {
            val alias = aliases[i]
            this.aliases[i] = alias
            this.setters[i] = propertyAccessStrategy.buildPropertyAccess(this.resultClass.java, alias).setter
        }
        this.isInitialized = true
    }

    fun check(aliases: Array<String>) =
        if (!aliases.contentEquals(this.aliases)) throw IllegalStateException("aliases are different from what is cached") else Unit

    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else if (other != null && this.javaClass == other.javaClass) {
            val that = other as AliasedObjectResultTransformer<*>
            if (this.resultClass != that.resultClass) {
                false
            } else {
                this.aliases.contentEquals(that.aliases)
            }
        } else false
    }

    override fun hashCode(): Int {
        var result = this.resultClass.hashCode()
        result = 31 * result + if (this.aliases.isNotEmpty()) this.aliases.contentHashCode() else 0
        return result
    }
}