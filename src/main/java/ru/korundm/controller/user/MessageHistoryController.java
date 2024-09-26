package ru.korundm.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import ru.korundm.dao.MessageHistoryService;
import ru.korundm.dao.MessageTypeService;
import ru.korundm.dao.UserService;
import ru.korundm.form.search.SearchMessageHistoryForm;

/**
 * Контроллер для работы с историей сообщений
 * @author zhestkov_an
 * Date:   09.01.2019
 */
@Controller
@SessionAttributes(names = "searchMessageHistoryForm", types = SearchMessageHistoryForm.class)
public class MessageHistoryController {

    private final MessageHistoryService messageHistoryService;
    private final MessageTypeService messageTypeService;
    private final UserService userService;

    public MessageHistoryController(
        MessageHistoryService messageHistoryService,
        MessageTypeService messageTypeService,
        UserService userService
    ) {
        this.messageHistoryService = messageHistoryService;
        this.messageTypeService = messageTypeService;
        this.userService = userService;
    }

    @GetMapping("/messageHistory")
    public String getMenuPage(ModelMap model) {
        model.addAttribute("messageHistoryList", messageHistoryService.getAll());
        return "section/messageHistory";
    }

    @PostMapping("/messageHistory")
    public String searchMessageHistoryMenu(@ModelAttribute("searchMessageHistoryForm") SearchMessageHistoryForm searchMessageHistoryForm, ModelMap model) {
        model.addAttribute("messageHistoryList", messageHistoryService.searchByParams(searchMessageHistoryForm.getType(), searchMessageHistoryForm.getUser(), searchMessageHistoryForm.getDateDeparture()));
        return "section/messageHistory";
    }

    @ModelAttribute
    public void setUpModelAttribute(ModelMap model) {
        if (!model.containsAttribute("searchMessageHistoryForm")) {
            SearchMessageHistoryForm scf = new SearchMessageHistoryForm();
            model.addAttribute("searchMessageHistoryForm", scf);
        }
        model.addAttribute("typeList", messageTypeService.getAll());
        model.addAttribute("userList", userService.getActiveAll());
    }
}
