package ru.korundm.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

/**
 * Конфигурация планировщика задач
 * Date:   29.11.2018
 */
@Configuration
@EnableScheduling
@ComponentScan("ru.korundm.schedule")
class ScheduleConfig {

    /*** Обработчик для запуска задач пользователем вручную */
    @Bean
    fun taskScheduler(): TaskScheduler {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.poolSize = 10
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler")
        return threadPoolTaskScheduler
    }
}