package ru.korundm.helper.jms;

import lombok.Getter;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import ru.korundm.configuration.JmsConfig;
import ru.korundm.entity.MessageTemplate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@JBossLog
public class JmsMessageMap implements Serializable {

    private final Map<String, String> data = new HashMap<>();
    private final MessageTemplate template;

    public JmsMessageMap(MessageTemplate template) {
        this.template = template;
    }

    public void putAttribute(String attribute, String value) {
        data.put(attribute, value);
    }

    public void jmsSend(JmsTemplate template) {
        try {
            template.convertAndSend(JmsConfig.DESTINATION_NAME, this);
        } catch (JmsException e) {
            log.error("Error in JMS receiver: " + e.getMessage());
        }
    }
}
