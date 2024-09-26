package ru.korundm.integration.onec.hr.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders


@Configuration
@PropertySource("classpath:onec.properties")
@ComponentScan("ru.korundm.integration.onec.hr")
class HRConfiguration(private val environment: Environment) {

    @Bean
    fun httpEntity(): HttpEntity<String> {
        val headers = HttpHeaders()
        headers.setBasicAuth(
            environment.getRequiredProperty("hr.username"),
            environment.getRequiredProperty("hr.password")
        )
        return HttpEntity<String>(headers)
    }
}