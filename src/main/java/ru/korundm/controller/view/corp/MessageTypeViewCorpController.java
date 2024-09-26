package ru.korundm.controller.view.corp;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.MessageTypeService;
import ru.korundm.entity.MessageType;
import ru.korundm.form.edit.EditMessageTypeForm;
import ru.korundm.form.search.MessageTypeListFilterForm;

@ViewController(RequestPath.View.Corp.MESSAGE_TYPE)
@SessionAttributes(names = "messageTypeListFilter" , types = MessageTypeListFilterForm.class)
public class MessageTypeViewCorpController {

    private static final String MESSAGE_TYPE_LIST_FILTER_FORM_ATTR = "messageTypeListFilter";

    private final MessageTypeService messageTypeService;

    public MessageTypeViewCorpController(MessageTypeService messageTypeService) {
        this.messageTypeService = messageTypeService;
    }

    @ModelAttribute(MESSAGE_TYPE_LIST_FILTER_FORM_ATTR)
    public MessageTypeListFilterForm messageTypeListFilterForm() {
        return new MessageTypeListFilterForm();
    }

    @GetMapping("/list")
    public String list() {
        return "corp/include/message-type/list";
    }

    // Редактирование элемента в списке категорий документов
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditMessageTypeForm form = new EditMessageTypeForm();
        if (id != null) {
            MessageType messageType = messageTypeService.read(id);
            form.setId(id);
            form.setName(messageType.getName());
            form.setDescription(messageType.getDescription());
            form.setCode(messageType.getCode());
        }
        model.addAttribute("form", form);
        return "corp/include/message-type/list/edit";
    }
}