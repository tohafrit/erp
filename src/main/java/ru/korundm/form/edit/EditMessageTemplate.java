package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.entity.MessageType;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

import static ru.korundm.util.FormValidatorUtil.formIdNotValid;

@Getter
@Setter
public class EditMessageTemplate implements Validatable {

    private Long id; // идентификатор
    private boolean active = Boolean.TRUE; // актуальность шаблона
    private String emailFrom; // от кого
    private String emailTo; // кому
    private String subject; // тема сообщения
    private String message; // сообщение
    private String cc; // копия (адреса через пробел)
    private String bcc; // скрытая копия (адреса через пробел)
    private MessageType messageType; // тип письма

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(emailFrom)) {
            errors.putError("emailFrom", ValidatorMsg.REQUIRED);
        }
        if (StringUtils.isBlank(emailTo)) {
            errors.putError("emailTo", ValidatorMsg.REQUIRED);
        }
        if (StringUtils.isBlank(subject)) {
            errors.putError("subject", ValidatorMsg.REQUIRED);
        }
        if (StringUtils.isBlank(message)) {
            errors.putError("message", ValidatorMsg.REQUIRED);
        }
        if (StringUtils.isBlank(emailFrom)) {
            errors.putError("emailFrom", ValidatorMsg.REQUIRED);
        }
        if (formIdNotValid(getMessageType())) {
            errors.putError("messageType", ValidatorMsg.REQUIRED);
        }
    }
}