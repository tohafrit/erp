package ru.korundm.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.korundm.helper.LuceneIndexer;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;

/**
 * Конфигурация работы с базой данных
 * !! Поддержка транзакций срабатывает только в тех бинах, которые были созданы {@link ComponentScan} вместе с объявлением
 * {@link EnableTransactionManagement}, так как с включением этой настройки срабатывает предварительное проксирование
 * транзакционных бинов
 * Date:   29.11.2019
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("ru.korundm.repository")
@ComponentScan("ru.korundm.dao")
public class PersistenceConfig {

    private static final String LUCENE_INDEX_PATH = System.getProperty("jboss.server.base.dir") + File.separator + "indexes" + File.separator;

    private final Environment environment;

    @Autowired
    public PersistenceConfig(Environment environment) {
        this.environment = environment;
    }

    /*** Менеджер транзакции */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager getJpaTransactionManager() {
        JpaTransactionManager jpa = new JpaTransactionManager();
        jpa.setEntityManagerFactory(getLocalContainerEntityManagerFactoryBean().getObject());
        return jpa;
    }

    /** Менеджер сущностей */
    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPackagesToScan(environment.getRequiredProperty("package.to.scan"));
        emf.setDataSource(getDataSource());

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);
        emf.setJpaProperties(additionalProperties());

        return emf;
    }

    @Bean
    public LuceneIndexer luceneIndexer(EntityManagerFactory entityManagerFactory) {
        // TODO в новой версии спринга/гибернейта сломалась инициализация
        /*LuceneIndexer luceneIndexer = new LuceneIndexer(entityManagerFactory);
        luceneIndexer.indexing();*/
        return null;
    }

    /*** Источник БД */
    @Bean(name = "dataSource")
    public DataSource getDataSource() {
        JndiDataSourceLookup lookup = new JndiDataSourceLookup();
        return lookup.getDataSource(environment.getRequiredProperty("jndi.name"));
    }

    /*** Методя для проставления свойств */
    private Properties additionalProperties() {
        Properties properties = new Properties();
        // SQL диалект
        properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        // включаем управление контекстом сессии
        properties.put("hibernate.current_session_context_class", environment.getRequiredProperty("hibernate.current_session_context_class"));
        // устанавливает максимальную "глубину" дерева выборки данных с использованием внешних соединений для неколлекционных ассоциаций (one-to-one, many-to-one)
        properties.put("hibernate.max_fetch_depth", environment.getRequiredProperty("hibernate.max_fetch_depth"));
        // задает размер выборки в драйвере
        properties.put("hibernate.jdbc.fetch_size", environment.getRequiredProperty("hibernate.jdbc.fetch_size"));
        // групповое обновление данных
        properties.put("hibernate.jdbc.batch_size", environment.getRequiredProperty("hibernate.jdbc.batch_size"));
        // вывод в консоль SQL-запросов
        properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        // форматировать выводимые в консоль SQL-запросы
        properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
        // определим положение полей со значением NULL при сортировке
        properties.put("hibernate.order_by.default_null_ordering", environment.getRequiredProperty("hibernate.order_by.default_null_ordering"));
        // подключение статистики (отладка приложения)
        properties.put("hibernate.generate_statistics", environment.getRequiredProperty("hibernate.generate_statistics"));
        // отключаем дополнительную/спящую валидацию
        properties.put("javax.persistence.validation.mode", environment.getRequiredProperty("javax.persistence.validation.mode"));
        // основные параметры для hibernate search
        properties.put("hibernate.search.default.directory_provider", environment.getRequiredProperty("hibernate.search.default.directory_provider"));
        properties.put("hibernate.search.default.indexBase", LUCENE_INDEX_PATH);

        // Hibernate Envers
        // суффикс имени таблицы для сущности, для которой отслеживаются версии
        properties.put("org.hibernate.envers.audit_table_suffix", environment.getRequiredProperty("audit_table_suffix"));
        // столбец таблицы хронологии для сохранения номера версии для каждой записи хронологии
        properties.put("org.hibernate.envers.revision_field_name", environment.getRequiredProperty("revision_field_name"));
        // столбец таблицы хронологии для сохранения типа действия обновления
        properties.put("org.hibernate.envers.revision_type_field_name", environment.getRequiredProperty("revision_type_field_name"));
        // стратегия аудита, используемая для отслеживания версий сущностей
        properties.put("org.hibernate.envers.audit_strategy", environment.getRequiredProperty("audit_strategy"));
        // столбец таблицы хронологии для сохранения номера конечной версии для каждой записи хронологии. Требуется только в случае применения стратегии аудита достоверности
        properties.put("org.hibernate.envers.audit_strategy_validity_end_rev_field_name", environment.getRequiredProperty("audit_strategy_validity_end_rev_field_name"));
        // следует ли сохранять метки времени при обновлении номера конечной версии для каждой записи хронологии. Требуется только в случае применения стратегии аудита достоверности
        properties.put("org.hibernate.envers.audit_strategy_validity_store_revend_timestamp", environment.getRequiredProperty("audit_strategy_validity_store_revend_timestamp"));
        // столбец таблицы хронологии для сохранения метки времени, когда обновляется номер конечной версии для каждой записи хронологии.
        // Требуется только в случае применения стратегии аудита достоверности и при условии, что предыдущее свойство установлено в true
        properties.put("org.hibernate.envers.audit_strategy_validity_revend_timestamp_field_name", environment.getRequiredProperty("audit_strategy_validity_revend_timestamp_field_name"));

        return properties;
    }
}