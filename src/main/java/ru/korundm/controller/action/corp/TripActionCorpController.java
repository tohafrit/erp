package ru.korundm.controller.action.corp;

import lombok.Getter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.MessageTemplateService;
import ru.korundm.dao.TripService;
import ru.korundm.dao.UserService;
import ru.korundm.entity.MessageTemplate;
import ru.korundm.entity.Trip;
import ru.korundm.entity.User;
import ru.korundm.enumeration.TripStatus;
import ru.korundm.enumeration.TripType;
import ru.korundm.form.edit.EditTripForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.helper.jms.JmsMessageMap;
import ru.korundm.schedule.MailMessage;
import ru.korundm.util.KtCommonUtil;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;

@ActionController(RequestPath.Action.Corp.TRIP)
public class TripActionCorpController {

    /** Универсальный код для отправки сообщения руководителю при добавлении личной командировки */
    private static final String TRIP_ADD_CODE = "ADD_TRIP";

    /** Универсальный код для отправки сообщения сотруднику о подтверждении командировки */
    private static final String TRIP_CONFIRMATION_CODE = "TRIP_CONFIRMATION";

    /** Универсальный код для отправки сообщения сотруднику об отказе в командировке */
    private static final String TRIP_NEITHERCONFIRM_CODE = "TRIP_NEITHERCONFIRM";

    /** Командировки за три месяца */
    private static final int TRIPS_FOR_THREE_MONTHS = 2;

    /** Форматирование даты */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final TripService tripService;
    private final UserService userService;
    private final MessageTemplateService messageTemplateService;
    private final JmsTemplate jmsTemplate;

    public TripActionCorpController(
        TripService tripService,
        UserService userService,
        MessageTemplateService messageTemplateService,
        JmsTemplate jmsTemplate
    ) {
        this.tripService = tripService;
        this.userService = userService;
        this.messageTemplateService = messageTemplateService;
        this.jmsTemplate = jmsTemplate;
    }

    @GetMapping("/list/load")
    public List<?> list_load(HttpSession session, ModelMap model) {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            String name; // причина
            LocalDate date; // дата
            LocalDate dateTo; // дата окончания
            LocalTime timeFrom; // время начала
            LocalTime timeTo; // время окончания
            String chief; // кто отпустил
            Boolean status; // одобрено
            String type; // тип
        }
        User user = KtCommonUtil.INSTANCE.getUser(session);
        LocalDate date = LocalDate.now().with(firstDayOfMonth());
        return tripService.getByEmployee(user, date).stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.name = item.getName();
            itemOut.date = item.getDate();
            itemOut.dateTo = item.getDateTo();
            itemOut.timeFrom = item.getTimeFrom();
            itemOut.timeTo = item.getTimeTo();
            itemOut.chief = item.getChief().getUserOfficialName();
            if (item.getStatus().equals(TripStatus.CONFIRMED)) {
                itemOut.status = Boolean.TRUE;
            } else if (item.getStatus().equals(TripStatus.REJECTED)) {
                itemOut.status = Boolean.FALSE;
            }
            itemOut.type = item.getType().getName();
            return itemOut;
        }).collect(Collectors.toList());
    }

    @GetMapping("/list/users/load")
    public List<?> list_users_load(HttpSession session, ModelMap model) {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            String name; // причина
            LocalDate date; // дата
            LocalDate dateTo; // дата окончания
            LocalTime timeFrom; // время начала
            LocalTime timeTo; // время окончания
            String employee; // сотрудник
            Boolean status; // одобрено
            String type; // тип
        }
        User user = KtCommonUtil.INSTANCE.getUser(session);
        LocalDate date = LocalDate.now().with(firstDayOfMonth());
        return tripService.getByChief(user, date).stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.name = item.getName();
            itemOut.date = item.getDate();
            itemOut.dateTo = item.getDateTo();
            itemOut.timeFrom = item.getTimeFrom();
            itemOut.timeTo = item.getTimeTo();
            itemOut.employee = item.getEmployee().getUserOfficialName();
            if (item.getStatus().equals(TripStatus.CONFIRMED)) {
                itemOut.status = Boolean.TRUE;
            } else if (item.getStatus().equals(TripStatus.REJECTED)) {
                itemOut.status = Boolean.FALSE;
            }
            itemOut.type = item.getType().getName();
            return itemOut;
        }).collect(Collectors.toList());
    }

    @PostMapping("/list/users/set-status")
    public void list_users_setStatus(
        TripStatus status,
        @RequestParam("tripIdList") List<Long> tripIdList
    ) {
        List<Trip> tripList = tripService.readAll(tripIdList).stream()
            .peek(trip -> trip.setStatus(status)).collect(Collectors.toList());
        tripService.saveAll(tripList);
        tripList.forEach(trip -> {
            MessageTemplate template = messageTemplateService
                .getByCode(trip.getStatus() == TripStatus.CONFIRMED ? TRIP_CONFIRMATION_CODE : TRIP_NEITHERCONFIRM_CODE);
            JmsMessageMap map = new JmsMessageMap(template);
            map.putAttribute(MailMessage.REASON, trip.getName());
            map.putAttribute(MailMessage.TIME_FROM, trip.getTimeFrom().toString());
            map.putAttribute(MailMessage.TIME_TO, trip.getTimeTo().toString());
            map.putAttribute(MailMessage.EMAIL_TO, trip.getChief().getEmail());
            map.putAttribute(MailMessage.EMAIL_FROM, trip.getEmployee().getEmail());
            map.putAttribute(MailMessage.DATE, trip.getDate().format(DATE_FORMATTER));
            map.jmsSend(jmsTemplate);
        });
    }

    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditTripForm form) throws MessagingException {
        ValidatorResponse response = new ValidatorResponse(form);
        Long formId = form.getId();
        if (response.isValid()) {
            Trip trip = formId != null ? tripService.read(formId) : new Trip();
            trip.setType(form.getType());
            trip.setName(form.getName());
            trip.setChief(userService.read(form.getChiefId()));
            trip.setEmployee(userService.read(form.getEmployeeId()));
            trip.setDate(form.getDate());
            trip.setTimeFrom(form.getTimeFrom());
            trip.setTimeTo(form.getTimeTo());
            if (form.getType().equals(TripType.BUSINESS)) {
                trip.setDateTo(form.getPeriod() ? form.getDateTo() : null);
            }
            trip.setStatus(TripStatus.ON_REVIEW);
            tripService.save(trip);
            if (formId == null) {
                response.putAttribute("addedTripId", trip.getId());
            }
            MessageTemplate template = messageTemplateService.getByCode(TRIP_ADD_CODE);
            JmsMessageMap map = new JmsMessageMap(template);
            map.putAttribute(MailMessage.EMAIL_FROM, trip.getChief().getEmail());
            map.putAttribute(MailMessage.EMAIL_TO, trip.getChief().getEmail());
            map.putAttribute(MailMessage.REASON, trip.getName());
            map.putAttribute(MailMessage.EMPLOYEES, trip.getEmployee().getUserOfficialName());
            map.putAttribute(MailMessage.DATE, trip.getDate().format(DATE_FORMATTER));
            map.putAttribute(MailMessage.TIME_FROM, trip.getTimeFrom().toString());
            map.putAttribute(MailMessage.TIME_TO, trip.getTimeTo().toString());
            map.putAttribute(MailMessage.LINK, "<a href=\"https://erp.korundm.local/trip\">ссылка</a>");
            map.jmsSend(jmsTemplate);
        }
        return response;
    }

    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        tripService.deleteById(id);
    }
}