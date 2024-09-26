package ru.korundm.controller.action.prod;

import lombok.Getter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.ComponentAppointmentService;
import ru.korundm.entity.ComponentAppointment;
import ru.korundm.helper.ValidatorResponse;

import java.util.List;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Prod.COMPONENT_APPOINTMENT)
public class ComponentAppointmentActionProdController {

    private final ComponentAppointmentService componentAppointmentService;

    public ComponentAppointmentActionProdController(
        ComponentAppointmentService componentAppointmentService
    ){
        this.componentAppointmentService = componentAppointmentService;
    }

    @GetMapping("/load")
    public List<?> load() {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            String name; // наименование
            String comment; // комментарий
        }
        return componentAppointmentService.getAll().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.name = item.getName();
            itemOut.comment = item.getComment();
            return itemOut;
        }).collect(Collectors.toList());
    }

    @PostMapping("/edit/save")
    public ValidatorResponse edit_save(ComponentAppointment componentAppointment) {
        ValidatorResponse response = new ValidatorResponse();
        //response.fill(HibernateUtil.entityValidate(componentAppointment));
        if (response.isValid()) {
            componentAppointmentService.save(componentAppointment);
        }
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        componentAppointmentService.deleteById(id);
    }
}