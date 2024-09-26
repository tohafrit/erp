package ru.korundm.controller.action.corp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.MessageTemplateService;
import ru.korundm.dao.MessageTypeService;
import ru.korundm.entity.MessageTemplate;
import ru.korundm.form.edit.EditMessageTemplate;
import ru.korundm.form.search.MessageTemplateListFilterForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrOut;
import ru.korundm.helper.TabrResultQuery;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static ru.korundm.util.FormValidatorUtil.assertFormId;

@ActionController(RequestPath.Action.Corp.MESSAGE_TEMPLATE)
@SessionAttributes(names = "messageTemplateListFilterForm", types = MessageTemplateListFilterForm.class)
public class MessageTemplateActionCorpController {

    private static final String MESSAGE_TEMPLATE_LIST_FILTER_FORM_ATTR = "messageTemplateListFilterForm";

    private final ObjectMapper jsonMapper;
    private final MessageTemplateService messageTemplateService;
    private final MessageTypeService messageTypeService;

    public MessageTemplateActionCorpController(
        ObjectMapper jsonMapper,
        MessageTemplateService messageTemplateService,
        MessageTypeService messageTypeService
    ) {
        this.jsonMapper = jsonMapper;
        this.messageTemplateService = messageTemplateService;
        this.messageTypeService = messageTypeService;
    }

    // Загрузка шаблонов почтовых сообщений
    @GetMapping("/list/load")
    public TabrOut<?> list_load(
        HttpServletRequest request,
        ModelMap model,
        String filterForm
    ) throws JsonProcessingException {
        @Getter
        class TableItemOut {
            long id; // идентификатор шаблона почтового сообщения
            String messageTypeName; // название типа сообщения
            Boolean active; // актуальность шаблона
            String subject; // тема сообщени
            String emailFrom; // от кого
            String emailTo; // кому
            String message; // сообщение
            String cc; // копия (адреса через пробел)
            String bcc; // скрытая копия (адреса через пробел)
        }
        MessageTemplateListFilterForm form = jsonMapper.readValue(filterForm, MessageTemplateListFilterForm.class);
        model.addAttribute(MESSAGE_TEMPLATE_LIST_FILTER_FORM_ATTR, form);
        TabrIn input = new TabrIn(request);
        TabrResultQuery<MessageTemplate> dataResultQuery = messageTemplateService.queryDataByFilterForm(input, form);
        List<TableItemOut> itemOutList = dataResultQuery.getData().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.messageTypeName = item.getType().getName();
            itemOut.active = item.isActive();
            itemOut.subject = item.getSubject();
            itemOut.emailFrom = item.getEmailFrom();
            itemOut.emailTo = item.getEmailTo();
            itemOut.message = item.getMessage();
            itemOut.cc = item.getCc();
            itemOut.bcc = item.getBcc();
            return itemOut;
        }).collect(Collectors.toList());
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), dataResultQuery.getCount());
        output.setData(itemOutList);
        return output;
    }

    @PostMapping("/list/edit/description")
    public String list_edit_description(Long id) {
        return id != null ? messageTypeService.read(id).getDescription() : null;
    }

    // Сохранение шаблона сообщения
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(
        EditMessageTemplate form
    ) {
        Long formId = form.getId();
        MessageTemplate messageTemplate = formId != null ? messageTemplateService.read(formId) : new MessageTemplate();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            messageTemplate.setActive(form.isActive());
            messageTemplate.setEmailFrom(form.getEmailFrom());
            messageTemplate.setEmailTo(form.getEmailTo());
            messageTemplate.setSubject(form.getSubject());
            messageTemplate.setMessage(form.getMessage());
            messageTemplate.setCc(StringUtils.defaultIfBlank(form.getCc(), null));
            messageTemplate.setBcc(StringUtils.defaultIfBlank(form.getBcc(), null));
            messageTemplate.setType(assertFormId(form.getMessageType()));
            messageTemplateService.save(messageTemplate);
        }
        return response;
    }

    // Удаление элемента из списка типов сообщений
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        messageTemplateService.deleteById(id);
    }
}