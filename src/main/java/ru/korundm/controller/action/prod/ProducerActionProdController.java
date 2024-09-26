package ru.korundm.controller.action.prod;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.ProducerService;
import ru.korundm.entity.Producer;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.util.HibernateUtil;

import java.util.List;

@ActionController(RequestPath.Action.Prod.PRODUCER)
public class ProducerActionProdController {

    private final ProducerService producerService;

    public ProducerActionProdController(ProducerService producerService) {
        this.producerService = producerService;
    }

    // Загрузка списка
    @GetMapping("/list/load")
    public List<Producer> list_load() {
        return producerService.getAll();
    }

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(Producer producer) {
        Long formId = producer.getId();
        ValidatorResponse response = new ValidatorResponse();
        response.fill(HibernateUtil.entityValidate(producer));
        if (response.isValid()) {
            producerService.save(producer);
            if (formId == null) {
                response.putAttribute("addedProducerId", producer.getId());
            }
        }
        return response;
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        producerService.deleteById(id);
    }
}