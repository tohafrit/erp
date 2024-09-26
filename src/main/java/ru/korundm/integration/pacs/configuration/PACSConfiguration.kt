package ru.korundm.integration.pacs.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import ru.korundm.constant.BaseConstant
import java.util.*

@Configuration
@EnableJpaRepositories(
    basePackages = ["ru.korundm.integration.pacs"],
    entityManagerFactoryRef = "pacsEntityManagerFactory"
)
@ComponentScan("ru.korundm.integration.pacs.dao")
class PACSConfiguration {
    @get:Bean(name = ["pacsEntityManagerFactory"])
    val localContainerEntityManagerFactoryBean: LocalContainerEntityManagerFactoryBean
        get() {
            val emf = LocalContainerEntityManagerFactoryBean()
            emf.setPackagesToScan("ru.korundm.integration.pacs.entity")
            emf.dataSource = JndiDataSourceLookup().getDataSource(BaseConstant.JNDI.PACS)
            val vendorAdapter: JpaVendorAdapter = HibernateJpaVendorAdapter()
            emf.jpaVendorAdapter = vendorAdapter
            val jpaProperties = Properties()
            jpaProperties["hibernate.dialect"] = "org.hibernate.dialect.SQLServerDialect" // SQL диалект
            jpaProperties["hibernate.current_session_context_class"] = "org.springframework.orm.hibernate5.SpringSessionContext" // включаем управление контекстом сессии
            jpaProperties["hibernate.max_fetch_depth"] = 3 // устанавливает максимальную "глубину" дерева выборки данных с использованием внешних соединений для неколлекционных ассоциаций (one-to-one, many-to-one)
            jpaProperties["hibernate.jdbc.fetch_size"] = 50 // задает размер выборки в драйвере
            jpaProperties["hibernate.jdbc.batch_size"] = 16 // групповое обновление данных
            jpaProperties["hibernate.show_sql"] = false // вывод в консоль SQL-запросов
            jpaProperties["hibernate.format_sql"] = false // форматировать выводимые в консоль SQL-запросы
            jpaProperties["hibernate.order_by.default_null_ordering"] = "last" // определим положение полей со значением NULL при сортировке
            jpaProperties["hibernate.generate_statistics"] = false // подключение статистики (отладка приложения)
            jpaProperties["javax.persistence.validation.mode"] = "none" // отключаем дополнительную/спящую валидаци
            jpaProperties["hibernate.enable_lazy_load_no_trans"] = true // для получения данных из проксированных объектов не требуется транзакция
            jpaProperties["hibernate.default_schema"] = "dbo" // схема по умолчанию
            emf.setJpaProperties(jpaProperties)
            return emf
        }
}