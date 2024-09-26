package ru.korundm.enumeration

import java.util.*

/**
 * Жизненный цикл компонентов
 * @author pakhunov_an
 * Date:   20.07.2020
 */
enum class ComponentLifecycle(val id: Long, val property: String) {

    NEW(1, "componentLifecycle.new"),
    DESIGN(2, "componentLifecycle.design"),
    INDUSTRIAL(3, "componentLifecycle.industrial");

    companion object {
        @JvmStatic
        fun getById(id: Long): ComponentLifecycle? = Arrays.stream(values()).filter { it.id == id }.findFirst().orElse(null)
    }
}