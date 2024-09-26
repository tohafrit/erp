package ru.korundm.controller.action.corp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.AdministrationOfficeDemandService;
import ru.korundm.dao.AdministrationOfficeStepService;
import ru.korundm.dao.MessageTemplateService;
import ru.korundm.dao.UserService;
import ru.korundm.entity.AdministrationOfficeDemand;
import ru.korundm.entity.AdministrationOfficeStep;
import ru.korundm.entity.MessageTemplate;
import ru.korundm.entity.User;
import ru.korundm.enumeration.AdministrationOfficeStatus;
import ru.korundm.form.edit.EditAdministrationOfficeDemandForm;
import ru.korundm.form.search.AdministrationOfficeDemandListFilterForm;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrOut;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.helper.jms.JmsMessageMap;
import ru.korundm.schedule.MailMessage;
import ru.korundm.util.KtCommonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Corp.ADMINISTRATION_OFFICE_DEMAND)
@SessionAttributes(names = "administrationOfficeDemandListFilterForm", types = AdministrationOfficeDemandListFilterForm.class)
public class AdministrationOfficeDemandActionCorpController {

    private static final String ADMINISTRATION_OFFICE_DEMAND_LIST_FILTER_FORM = "administrationOfficeDemandListFilterForm";

    /** Исполнитель заявок по умолчанию */
    private static final String DEFAULT_EXECUTOR_USER_LOGIN = "poteshenkov_al";

    /** Исполнитель заявок из другого отдела */
    private static final String OTHER_USER_LOGIN = "bunin_pk";

    /** Идентификатор отдела АХО */
    private static final long DEFAULT_DEPARTMENT_ID = 8;

    /** Универсальный код для отправки сообщения в АХО */
    private static final String ADMINISTRATION_OFFICE = "ADMINISTRATION_OFFICE";

    private final ObjectMapper jsonMapper;
    private final AdministrationOfficeDemandService administrationOfficeDemandService;
    private final AdministrationOfficeStepService administrationOfficeStepService;
    private final UserService userService;
    private final JmsTemplate jmsTemplate;
    private final MessageTemplateService messageTemplateService;

    public AdministrationOfficeDemandActionCorpController(
        ObjectMapper jsonMapper,
        AdministrationOfficeDemandService administrationOfficeDemandService,
        AdministrationOfficeStepService administrationOfficeStepService,
        UserService userService,
        JmsTemplate jmsTemplate,
        MessageTemplateService messageTemplateService
    ) {
        this.jsonMapper = jsonMapper;
        this.administrationOfficeDemandService = administrationOfficeDemandService;
        this.administrationOfficeStepService = administrationOfficeStepService;
        this.userService = userService;
        this.jmsTemplate = jmsTemplate;
        this.messageTemplateService = messageTemplateService;
    }

    @GetMapping("/list/load")
    public List<?> list_load(HttpSession session, ModelMap model) {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            LocalDateTime requestDate; // время заявки
            String user; // имя пользователя
            String roomNumber; // номер комнаты
            String reason; // причина
            String statusText; // текст статуса
            AdministrationOfficeStatus status; // статус
        }
        User user = KtCommonUtil.INSTANCE.getUser(session);
        return administrationOfficeDemandService.getAllByUser(user).stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.requestDate = item.getRequestDate();
            itemOut.user = item.getUser().getUserOfficialName();
            itemOut.roomNumber = item.getRoomNumber();
            itemOut.reason = item.getReason();
            itemOut.status = administrationOfficeStepService.getLastByDemand(itemOut.id).getStatus();
            itemOut.statusText = itemOut.getStatus().getProperty();
            return itemOut;
        }).collect(Collectors.toList());
    }

    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(
        HttpSession session,
        ModelMap model,
        EditAdministrationOfficeDemandForm form
    ) {
        User user = KtCommonUtil.INSTANCE.getUser(session);
        ValidatorResponse response = new ValidatorResponse(form);
        Long formId = form.getId();
        if (response.isValid()) {
            AdministrationOfficeDemand administrationOfficeDemand;
            if (formId == null) {
                administrationOfficeDemand = new AdministrationOfficeDemand();
                administrationOfficeDemand.setUser(user);
                administrationOfficeDemand.setRequestDate(LocalDateTime.now());
            } else {
                administrationOfficeDemand = administrationOfficeDemandService.read(formId);
            }
            administrationOfficeDemand.setRoomNumber(form.getRoomNumber());
            administrationOfficeDemand.setReason(form.getReason());
            administrationOfficeDemandService.save(administrationOfficeDemand);
            AdministrationOfficeStep administrationOfficeStep = new AdministrationOfficeStep();
            administrationOfficeStep.setDemand(administrationOfficeDemand);
            administrationOfficeStep.setStatus(AdministrationOfficeStatus.NEW);
            administrationOfficeStep.setExecutor(userService.findByUserName(DEFAULT_EXECUTOR_USER_LOGIN));
            administrationOfficeStep.setTime(LocalDateTime.now());
            administrationOfficeStepService.save(administrationOfficeStep);
            if (formId == null) {
                response.putAttribute("addedDemandId", administrationOfficeDemand.getId());
                MessageTemplate template = messageTemplateService.getByCode(ADMINISTRATION_OFFICE);
                JmsMessageMap map = new JmsMessageMap(template);
                map.putAttribute(MailMessage.EMAIL_FROM, administrationOfficeDemand.getUser().getEmail());
                map.putAttribute(MailMessage.REASON, administrationOfficeDemand.getReason());
                map.putAttribute(MailMessage.EMPLOYEE, administrationOfficeDemand.getUser().getUserOfficialName());
                map.putAttribute(MailMessage.LINK, "<a href=\"https://erp.korundm.local/administration-office\">ссылка</a>");
                map.jmsSend(jmsTemplate);
            }
        }
        return response;
    }

    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        administrationOfficeDemandService.deleteById(id);
    }

    @GetMapping("/admin/load")
    public TabrOut<?> admin_load(
        HttpServletRequest request,
        ModelMap model,
        String filterForm
    ) throws JsonProcessingException {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            LocalDateTime requestDate; // дата и время заявки
            String user; // имя пользователя
            String roomNumber; // номер комнаты
            String reason; // причина
            String statusText; // текст статуса
            AdministrationOfficeStatus status; // статус
        }
        TabrIn input = new TabrIn(request);
        AdministrationOfficeDemandListFilterForm form = jsonMapper.readValue(filterForm, AdministrationOfficeDemandListFilterForm.class);
        model.addAttribute(ADMINISTRATION_OFFICE_DEMAND_LIST_FILTER_FORM, form);
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), administrationOfficeDemandService.getCountByForm(form));
        List<TableItemOut> itemOutList = administrationOfficeDemandService.getByTableDataIn(input, form).stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.requestDate = item.getRequestDate();
            itemOut.user = item.getUser().getUserOfficialName();
            itemOut.roomNumber = item.getRoomNumber();
            itemOut.reason = item.getReason();
            itemOut.status = administrationOfficeStepService.getLastByDemand(itemOut.id).getStatus();
            itemOut.statusText = itemOut.getStatus().getProperty();
            return itemOut;
        }).collect(Collectors.toList());
        output.setData(itemOutList);
        return output;
    }

    @GetMapping("/admin/detail/load")
    public List<?> admin_detail_load(long id) {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            String executor; // исполнитель
            LocalDateTime updatedOn; // время изменения
            String note; // заметка
            String statusText; // текст статуса
            AdministrationOfficeStatus status; // статус
            boolean changeable; // статус можно редактировать или изменить
        }
        List<TableItemOut> itemOutList = administrationOfficeDemandService.read(id).getStepList().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.executor = item.getExecutor().getUserOfficialName();
            itemOut.updatedOn = item.getTime();
            itemOut.note = item.getNote();
            itemOut.statusText = item.getStatus().getProperty();
            itemOut.status = item.getStatus();
            return itemOut;
        }).collect(Collectors.toList());
        // логика разрешения редактирования статуса
        // может быть дополнена запретом на редактирование чужих статусов или спустя время после выставления
        if (CollectionUtils.isNotEmpty(itemOutList)) {
            TableItemOut itemOut = itemOutList.get(0);
            if (itemOut.status != AdministrationOfficeStatus.NEW) {
                itemOut.changeable = true;
            }
        }
        return itemOutList;
    }

    @PostMapping("/admin/detail/set-status/save")
    public void admin_detail_setStatus_save(
        long demandId,
        long executorId,
        AdministrationOfficeStatus status,
        String note,
        @RequestParam(required = false) Long stepId
    ) {
        AdministrationOfficeDemand administrationOfficeDemand = administrationOfficeDemandService.read(demandId);
        AdministrationOfficeStep step = stepId != null ?
            administrationOfficeStepService.read(stepId) : new AdministrationOfficeStep();
        step.setDemand(administrationOfficeDemand);
        step.setExecutor(userService.read(executorId));
        step.setStatus(status);
        step.setNote(note);
        step.setTime(LocalDateTime.now());
        administrationOfficeStepService.save(step);
    }

    @DeleteMapping("/admin/detail/delete/{id}")
    public void list_delete(@PathVariable long id) {
        administrationOfficeStepService.deleteById(id);
    }
}