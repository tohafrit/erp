package ru.korundm.helper

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * Помощник Spring для добавления инъекций
 * @author pakhunov_an
 * Date:   07.02.2018
 */
object AutowireHelper : ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    @JvmStatic
    fun autowire(obj: Any) = this.applicationContext.autowireCapableBeanFactory.autowireBean(obj)
}