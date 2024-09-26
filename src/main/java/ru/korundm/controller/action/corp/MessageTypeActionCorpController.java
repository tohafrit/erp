package ru.korundm.controller.action.corp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.MessageTypeService;
import ru.korundm.entity.MessageType;
import ru.korundm.form.edit.EditMessageTypeForm;
import ru.korundm.form.search.MessageTypeListFilterForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrOut;
import ru.korundm.helper.TabrResultQuery;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Corp.MESSAGE_TYPE)
@SessionAttributes(names = "messageTypeListFilter", types = MessageTypeListFilterForm.class)
public class MessageTypeActionCorpController {

    private static final String MESSAGE_TYPE_LIST_FILTER_FORM_ATTR = "messageTypeListFilter";

    private final ObjectMapper jsonMapper;
    private final MessageTypeService messageTypeService;

    public MessageTypeActionCorpController(
        ObjectMapper jsonMapper,
        MessageTypeService messageTypeService
    ) {
        this.jsonMapper = jsonMapper;
        this.messageTypeService = messageTypeService;
    }

    // Загрузка типов сообщений
    @GetMapping("/list/load")
    public TabrOut<?> list_load(
        HttpServletRequest request,
        ModelMap model,
        String filterForm
    ) throws JsonProcessingException {
        @Getter
        class TableItemOut {
            long id; // идентификатор договора
            String name; // название типа сообщения
            String description; // описание типа сообщения, может хранить передаваемые параментры по шаблону #FIELD_NAME#
            String code; // уникальный код типа сообщения
        }
        MessageTypeListFilterForm form = jsonMapper.readValue(filterForm, MessageTypeListFilterForm.class);
        model.addAttribute(MESSAGE_TYPE_LIST_FILTER_FORM_ATTR, form);
        TabrIn input = new TabrIn(request);
        TabrResultQuery<MessageType> dataResultQuery = messageTypeService.queryDataByFilterForm(input, form);
        List<TableItemOut> itemOutList = dataResultQuery.getData().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.name = item.getName();
            itemOut.description = item.getDescription();
            itemOut.code = item.getCode();
            return itemOut;
        }).collect(Collectors.toList());
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), dataResultQuery.getCount());
        output.setData(itemOutList);
        return output;
    }

    // Сохранение типа сообщения
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(
        EditMessageTypeForm form
    ) {
        Long formId = form.getId();
        MessageType messageType = formId != null ? messageTypeService.read(formId) : new MessageType();
        form.setCheckUniqueCode(messageTypeService.isUniqueCode(formId, form.getCode()));
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            messageType.setName(form.getName().trim());
            messageType.setCode(StringUtils.defaultIfBlank(form.getCode().toUpperCase(), null));
            messageType.setDescription(StringUtils.defaultIfBlank(form.getDescription(), null));
            messageTypeService.save(messageType);
        }
        return response;
    }

    // Удаление элемента из списка типов сообщений
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        messageTypeService.deleteById(id);
    }
}