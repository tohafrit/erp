package ru.korundm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.jms.ConnectionFactory;
import java.util.Objects;

/**
 * Конфигурация для работы c Jms
 * Date:   30.05.2018
 */
@Configuration
@EnableJms
@ComponentScan("ru.korundm.helper.jms")
public class JmsConfig {

    /** Очередь для JMS */
    public static final String QUEUE_DESTINATION = "java:/jms/queue/erpMessage";

    /** Обьект, ответственный за создание JMS Connection */
    private static final String CONNECTION_FACTORY = "java:/ConnectionFactory";

    public static final String DESTINATION_NAME = "ErpMessage";

    /** Ограничение по многопоточности */
    private static final String CONCURRENCY_LIMIT = "3-10";

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setDestinationResolver(new JndiDestinationResolver());
        factory.setSessionTransacted(true);
        factory.setConcurrency(CONCURRENCY_LIMIT);
        factory.setAutoStartup(true);
        return factory;
    }

    @Bean
    public JndiObjectFactoryBean jndiObjectFactoryBean() {
        JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName(CONNECTION_FACTORY);
        jndi.setLookupOnStartup(true);
        jndi.setProxyInterface(ConnectionFactory.class);
        return jndi;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new SingleConnectionFactory((ConnectionFactory) Objects.requireNonNull(jndiObjectFactoryBean().getObject()));
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setDefaultDestinationName(QUEUE_DESTINATION);
        return jmsTemplate;
    }
}