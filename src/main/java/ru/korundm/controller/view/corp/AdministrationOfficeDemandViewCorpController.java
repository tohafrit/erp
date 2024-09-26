package ru.korundm.controller.view.corp;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.AdministrationOfficeDemandService;
import ru.korundm.dao.AdministrationOfficeStepService;
import ru.korundm.dao.MessageTemplateService;
import ru.korundm.dao.UserService;
import ru.korundm.entity.AdministrationOfficeDemand;
import ru.korundm.entity.User;
import ru.korundm.enumeration.AdministrationOfficeStatus;
import ru.korundm.form.edit.EditAdministrationOfficeDemandForm;
import ru.korundm.form.search.AdministrationOfficeDemandListFilterForm;
import ru.korundm.schedule.MailMessage;

import java.util.ArrayList;
import java.util.List;

@ViewController(RequestPath.View.Corp.ADMINISTRATION_OFFICE_DEMAND)
@SessionAttributes(names = "administrationOfficeDemandListFilterForm", types = AdministrationOfficeDemandListFilterForm.class)
public class AdministrationOfficeDemandViewCorpController {

    private static final String ADMINISTRATION_OFFICE_DEMAND_LIST_FILTER_FORM = "administrationOfficeDemandListFilterForm";

    /** Исполнитель заявок по умолчанию */
    private static final String DEFAULT_EXECUTOR_USER_LOGIN = "poteshenkov_al";

    /** Исполнитель заявок из другого отдела */
    private static final String OTHER_USER_LOGIN = "bunin_pk";

    /** Идентификатор отдела АХО */
    private static final long DEFAULT_DEPARTMENT_ID = 8;

    /** Универсальный код для отправки сообщения в АХО */
    private static final String ADMINISTRATION_OFFICE = "ADMINISTRATION_OFFICE";

    private final AdministrationOfficeDemandService administrationOfficeDemandService;
    private final AdministrationOfficeStepService administrationOfficeStepService;
    private final UserService userService;
    private final MessageTemplateService messageTemplateService;
    private final MailMessage mailMessage;

    public AdministrationOfficeDemandViewCorpController(
        AdministrationOfficeDemandService administrationOfficeDemandService,
        AdministrationOfficeStepService administrationOfficeStepService,
        UserService userService,
        MessageTemplateService messageTemplateService,
        MailMessage mailMessage
    ) {
        this.administrationOfficeDemandService = administrationOfficeDemandService;
        this.administrationOfficeStepService = administrationOfficeStepService;
        this.userService = userService;
        this.messageTemplateService = messageTemplateService;
        this.mailMessage = mailMessage;
    }

    @ModelAttribute(ADMINISTRATION_OFFICE_DEMAND_LIST_FILTER_FORM)
    public AdministrationOfficeDemandListFilterForm administrationOfficeDemandListFilterFormAttr() {
        return new AdministrationOfficeDemandListFilterForm();
    }

    @GetMapping("/list")
    public String list() {
        return "corp/include/administration-office-demand/list";
    }

    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditAdministrationOfficeDemandForm form = new EditAdministrationOfficeDemandForm();
        if (id != null) {
            AdministrationOfficeDemand administrationOfficeDemand = administrationOfficeDemandService.read(id);
            form.setId(administrationOfficeDemand.getId());
            form.setRoomNumber(administrationOfficeDemand.getRoomNumber());
            form.setReason(administrationOfficeDemand.getReason());
        }
        model.addAttribute("form", form);
        return "corp/include/administration-office-demand/list/edit";
    }

    @GetMapping("/admin")
    public String admin() {
        return "corp/include/administration-office-demand/admin";
    }

    @GetMapping("/admin/filter")
    public String admin_filter(ModelMap model) {
        model.addAttribute("userList", userService.getActiveAll());
        return "corp/include/administration-office-demand/admin/filter";
    }

    @GetMapping("/admin/detail")
    public String admin_detail(ModelMap model, Long entityId) {
        model.addAttribute("entityId", entityId);
        return "corp/include/administration-office-demand/admin/detail";
    }

    @GetMapping("/admin/detail/set-status")
    public String admin_detail_setStatus(
        ModelMap model,
        Long id,
        @RequestParam(required = false) Long stepId
    ) {
        // TODO позже переделать используя отдел
        List<User> ahoWorkersList = new ArrayList<>();
        ahoWorkersList.add(userService.findByUserName("vedernikova_va"));
        ahoWorkersList.add(userService.findByUserName("vorobiev_kv"));
        ahoWorkersList.add(userService.findByUserName("vorobieva_lv"));
        ahoWorkersList.add(userService.findByUserName("guseinova_aa"));
        ahoWorkersList.add(userService.findByUserName("gushin_ay"));
        ahoWorkersList.add(userService.findByUserName("korobova_le"));
        model.addAttribute("ahoWorkersList", ahoWorkersList);
        model.addAttribute("statusList", AdministrationOfficeStatus.getAllButNew());
        model.addAttribute("demandId", id);
        model.addAttribute("stepId", stepId);
        return "corp/include/administration-office-demand/admin/detail/setStatus";
    }
}