package ru.korundm.helper.jms;

import lombok.extern.jbosslog.JBossLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ru.korundm.configuration.JmsConfig;
import ru.korundm.schedule.MailMessage;

@Component
@JBossLog
public class Receiver {

    @Autowired
    private MailMessage mailMessage;

    @JmsListener(containerFactory = "jmsListenerContainerFactory", destination = JmsConfig.QUEUE_DESTINATION)
    public void receive(JmsMessageMap data) {
        mailMessage.sendMessageTemplate(data.getData(), data.getTemplate());
    }
}