package ru.korundm.controller.view.corp;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.MessageTemplateService;
import ru.korundm.dao.MessageTypeService;
import ru.korundm.entity.MessageTemplate;
import ru.korundm.form.edit.EditMessageTemplate;
import ru.korundm.form.search.MessageTemplateListFilterForm;

@ViewController(RequestPath.View.Corp.MESSAGE_TEMPLATE)
@SessionAttributes(names = "messageTemplateListFilterForm" , types = MessageTemplateListFilterForm.class)
public class MessageTemplateViewCorpController {

    private static final String MESSAGE_TEMPLATE_LIST_FILTER_FORM_ATTR = "messageTemplateListFilterForm";

    private final MessageTemplateService messageTemplateService;
    private final MessageTypeService messageTypeService;

    public MessageTemplateViewCorpController(
        MessageTemplateService messageTemplateService,
        MessageTypeService messageTypeService
    ) {
        this.messageTemplateService = messageTemplateService;
        this.messageTypeService = messageTypeService;
    }

    @ModelAttribute(MESSAGE_TEMPLATE_LIST_FILTER_FORM_ATTR)
    public MessageTemplateListFilterForm messageTemplateListFilterForm() {
        return new MessageTemplateListFilterForm();
    }

    @GetMapping("/list")
    public String list() {
        return "corp/include/message-template/list";
    }

    // Редактирование/добавление шаблона сообщения
    @GetMapping("/list/edit")
    public String list_edit(
        ModelMap model,
        Long id
    ) {
        EditMessageTemplate form = new EditMessageTemplate();
        if (id != null) {
            MessageTemplate messageTemplate = messageTemplateService.read(id);
            form.setId(messageTemplate.getId());
            form.setActive(messageTemplate.isActive());
            form.setEmailFrom(messageTemplate.getEmailFrom());
            form.setEmailTo(messageTemplate.getEmailTo());
            form.setSubject(messageTemplate.getSubject());
            form.setMessage(messageTemplate.getMessage());
            form.setCc(messageTemplate.getCc());
            form.setBcc(messageTemplate.getBcc());
            form.setMessageType(messageTemplate.getType());
        }
        model.addAttribute("form", form);
        model.addAttribute("messageTypeList", messageTypeService.getAll());
        return "corp/include/message-template/list/edit";
    }
}