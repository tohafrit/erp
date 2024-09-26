package ru.korundm.controller.view.corp;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.TripService;
import ru.korundm.dao.UserService;
import ru.korundm.entity.Trip;
import ru.korundm.entity.User;
import ru.korundm.enumeration.TripType;
import ru.korundm.form.edit.EditTripForm;
import ru.korundm.util.KtCommonUtil;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;

@ViewController(RequestPath.View.Corp.TRIP)
public class TripViewCorpController {

    private final TripService tripService;
    private final UserService userService;

    public TripViewCorpController(
        TripService tripService,
        UserService userService
    ) {
        this.tripService = tripService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public String list(HttpSession session, ModelMap model) {
        User user = KtCommonUtil.INSTANCE.getUser(session);
        LocalDate date = LocalDate.now().with(firstDayOfMonth());
        model.addAttribute("isEmployeeTrip", tripService.existByChief(user, date));
        return "corp/include/trip/list";
    }

    @GetMapping("/list/edit")
    public String edit(HttpSession session, ModelMap model, Long id) {
        User user = KtCommonUtil.INSTANCE.getUser(session);
        EditTripForm form = new EditTripForm();
        if (id != null) {
            Trip trip = tripService.read(id);
            form.setId(trip.getId());
            form.setName(trip.getName());
            form.setChiefId(trip.getChief().getId());
            form.setEmployeeId(trip.getEmployee().getId());
            form.setDate(trip.getDate());
            form.setTimeFrom(trip.getTimeFrom());
            form.setTimeTo(trip.getTimeTo());
            form.setType(trip.getType());
            if (trip.getType().equals(TripType.BUSINESS) && trip.getDateTo() != null) {
                form.setDateTo(trip.getDateTo());
                form.setPeriod(Boolean.TRUE);
            }
            else {
                form.setPeriod(Boolean.FALSE);
            }
        } else {
            form.setTimeFrom(LocalTime.of(7, 0, 0)); // минимальное начало рабочего дня
            form.setTimeTo(LocalTime.of(19, 0, 0)); // максимальное окончание рабочего дня
            form.setEmployeeId(user.getId());
            form.setPeriod(Boolean.TRUE);
            form.setType(TripType.BUSINESS);
        }
        model.addAttribute("form", form);
        model.addAttribute("userList", userService.getActiveAll());
        model.addAttribute("typeList", TripType.values());
        return "corp/include/trip/list/edit";
    }

    @GetMapping("/list/users")
    public String list_users() {
        return "corp/include/trip/list/users";
    }
}