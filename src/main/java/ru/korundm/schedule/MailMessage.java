package ru.korundm.schedule;

import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.korundm.entity.MessageTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Работа с сообщениями
 * @author pakhunov_an
 * Date:   24.12.2018
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@JBossLog
public final class MailMessage {

    private final JavaMailSender emailSender;

    /** Расписание (каждые 30 минут) */
    private static final String CRON = "0 */30 * * * *";

    public static final Set<String> errorSet = new HashSet<>();

    /** Ключи */
    public static final String REASON = "#REASON#";
    public static final String TIME_FROM = "#TIME_FROM#";
    public static final String TIME_TO = "#TIME_TO#";
    public static final String EMAIL_TO = "#EMAIL_TO#";
    public static final String EMAIL_FROM = "#EMAIL_FROM#";
    public static final String DATE = "#DATE#";
    public static final String EMPLOYEE = "#EMPLOYEE#";
    public static final String EMPLOYEES = "#EMPLOYEES#";
    public static final String LINK = "#LINK#";

    public MailMessage(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * Отправка сообщений об ошибках по расписанию
     * @throws MessagingException
     */
    @Scheduled(cron = CRON)
    public void scheduleMailSender() throws MessagingException {
        if (!errorSet.isEmpty()) {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("Erp Support <oa_support@korund-m.ru>");
            helper.setTo("oa_support@korund-m.ru");
            helper.setSubject("Erp: Список ошибок");
            StringBuilder text = new StringBuilder();
            errorSet.forEach(e -> text.append("<p>").append(e).append("</p>"));
            helper.setText(text.toString(), true);
            emailSender.send(message);
            errorSet.clear();
        }
    }

    /**
     * Подготовка и отправка сообщения по шаблону
     * @param fields
     * @param messageTemplate
     */
    public void sendMessageTemplate(Map<String, String> fields, MessageTemplate messageTemplate) {
        MimeMessage messages = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(messages);
        String message = messageTemplate.getMessage();
        String subject = messageTemplate.getSubject();
        String emailFrom = messageTemplate.getEmailFrom();
        String emailTo = messageTemplate.getEmailTo();
        String cc = messageTemplate.getCc();
        String bcc = messageTemplate.getBcc();
        for (String key: fields.keySet()) {
            String field = fields.get(key);
            message = message.replaceAll(key, field);
            subject = subject.replaceAll(key, field);
            emailFrom = emailFrom.replaceAll(key, field);
            emailTo = emailTo.replaceAll(key, field);
            cc = cc.replaceAll(key, field);
            bcc = bcc.replaceAll(key, field);
        }
        if (StringUtils.isNotBlank(emailFrom) && StringUtils.isNotBlank(emailTo)) {
            try {
                helper.setFrom(emailFrom);
                helper.setTo(emailTo);
                helper.setText(message, true);
                helper.setSubject(subject);
                if (StringUtils.isNotBlank(cc)) {
                    helper.setCc(cc);
                }
                if (StringUtils.isNotBlank(bcc)) {
                    helper.setBcc(bcc);
                }
                emailSender.send(messages);
            } catch (MailException | MessagingException e) {
                log.error("Mail error: " + e.getMessage());
            }
        }
    }
}