package ru.korundm.configuration

import asu.configuration.AsuConfiguration
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import eco.configuration.EcoConfiguration
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.*
import org.springframework.core.env.Environment
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.web.client.RestTemplate
import ru.korundm.integration.onec.hr.configuration.HRConfiguration
import ru.korundm.integration.pacs.configuration.PACSConfiguration

/**
 * Общая конфигурация приложения
 * Date:   04.05.2018
 */
@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan("ru.korundm.entity.listener", "ru.korundm.helper.manager")
@Import(
    ScheduleConfig::class, PersistenceConfig::class, JmsConfig::class, EcoConfiguration::class, AsuConfiguration::class,
    PACSConfiguration::class, HRConfiguration::class
)
class AppConfig(private val environment: Environment) {

    /*** Возвращает объект сериализации/десериализации JSON */
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    fun jsonMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.registerModule(KotlinModule())
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
        return mapper
    }

    /*** Метод для формирования сервиса отправки почты */
    @Bean
    fun javaMailSender(): JavaMailSender {
        val sender = JavaMailSenderImpl()
        sender.host = environment.getRequiredProperty("mail.host")
        sender.port = environment.getRequiredProperty("mail.port").toInt()
        sender.username = environment.getRequiredProperty("mail.username")
        sender.password = environment.getRequiredProperty("mail.password")
        sender.defaultEncoding = environment.getRequiredProperty("mail.encoding")
        val properties = sender.javaMailProperties
        properties["mail.store.protocol"] = environment.getRequiredProperty("mail.store.protocol")
        properties["mail.imap.socketFactory.class"] = environment.getRequiredProperty("mail.imap.socketFactory.class")
        properties["mail.imap.socketFactory.fallback"] = environment.getRequiredProperty("mail.imap.socketFactory.fallback")
        properties["mail.debug"] = environment.getRequiredProperty("mail.debug")
        return sender
    }

    /** Для взаимодействия с RESTful сервисами */
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}