package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.ComponentAppointmentService;
import ru.korundm.entity.ComponentAppointment;
import ru.korundm.constant.RequestPath;

@ViewController(RequestPath.View.Prod.COMPONENT_APPOINTMENT)
public class ComponentAppointmentViewProdController {

    private final ComponentAppointmentService componentAppointmentService;

    public ComponentAppointmentViewProdController(
        ComponentAppointmentService componentAppointmentService
    ){
        this.componentAppointmentService = componentAppointmentService;
    }

    @GetMapping("/edit")
    public String edit(ModelMap model, Long id) {
        ComponentAppointment componentAppointment = id != null ? componentAppointmentService.read(id) : new ComponentAppointment();
        model.addAttribute("componentAppointment", componentAppointment);
        return "prod/include/component-appointment/edit";
    }
}