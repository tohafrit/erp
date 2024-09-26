package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.ProducerService;
import ru.korundm.entity.Producer;

@ViewController(RequestPath.View.Prod.PRODUCER)
public class ProducerViewProdController {

    private final ProducerService producerService;

    public ProducerViewProdController(ProducerService producerService) {
        this.producerService = producerService;
    }

    // Страница списка
    @GetMapping("/list")
    public String list() {
        return "prod/include/producer/list";
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        Producer producer = id != null ? producerService.read(id) : new Producer();
        model.addAttribute("producer", producer);
        return "prod/include/producer/list/edit";
    }
}