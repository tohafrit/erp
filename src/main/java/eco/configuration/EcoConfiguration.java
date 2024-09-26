package eco.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import ru.korundm.constant.BaseConstant;

import java.util.Properties;

/**
 * Конфигурация подключения к БД ECO
 * Date:   29.11.2018
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "eco.repository",
    entityManagerFactoryRef = "ecoEntityManagerFactory"
)
@ComponentScan("eco.dao")
public class EcoConfiguration {

    @Bean(name = "ecoEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPackagesToScan("eco.entity");
        emf.setDataSource(new JndiDataSourceLookup().getDataSource(BaseConstant.JNDI.ECO));

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect"); // SQL диалект
        jpaProperties.put("hibernate.current_session_context_class", "org.springframework.orm.hibernate5.SpringSessionContext"); // включаем управление контекстом сессии
        jpaProperties.put("hibernate.max_fetch_depth", 3); // устанавливает максимальную "глубину" дерева выборки данных с использованием внешних соединений для неколлекционных ассоциаций (one-to-one, many-to-one)
        jpaProperties.put("hibernate.jdbc.fetch_size", 50); // задает размер выборки в драйвере
        jpaProperties.put("hibernate.jdbc.batch_size", 16); // групповое обновление данных
        jpaProperties.put("hibernate.show_sql", false); // вывод в консоль SQL-запросов
        jpaProperties.put("hibernate.format_sql", false); // форматировать выводимые в консоль SQL-запросы
        jpaProperties.put("hibernate.order_by.default_null_ordering", "last"); // определим положение полей со значением NULL при сортировке
        jpaProperties.put("hibernate.generate_statistics", false); // подключение статистики (отладка приложения)
        jpaProperties.put("javax.persistence.validation.mode", "none"); // отключаем дополнительную/спящую валидаци
        jpaProperties.put("hibernate.enable_lazy_load_no_trans", true); // для получения данных из проксированных объектов не требуется транзакция
        jpaProperties.put("hibernate.default_schema", "ECOPLAN"); // схема по умолчанию
        emf.setJpaProperties(jpaProperties);
        return emf;
    }
}