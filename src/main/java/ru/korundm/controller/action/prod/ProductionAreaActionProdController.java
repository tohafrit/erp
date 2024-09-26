package ru.korundm.controller.action.prod;

import lombok.Getter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.ProductionAreaService;
import ru.korundm.entity.ProductionArea;
import ru.korundm.exception.AlertUIException;
import ru.korundm.form.edit.EditProductionAreaForm;
import ru.korundm.helper.ValidatorResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Prod.PRODUCTION_AREA)
public class ProductionAreaActionProdController {

    private final ProductionAreaService productionAreaService;

    public ProductionAreaActionProdController(
        ProductionAreaService productionAreaService
    ) {
        this.productionAreaService = productionAreaService;
    }

    // Загрузка списка
    @GetMapping("/list/load")
    public List<?> list_load() {
        @Getter
        class ResponseOut {
            long id; // идентификатор
            String name; // наименование
            String code; // код
            boolean hasEmployee; // наличие закрепленных сотрудников
            boolean hasStoreroom; // наличие производственных кладовок
            boolean hasDefect; // наличие производственных дефектов
            boolean technological; // технологическая
        }
        return productionAreaService.getAll().stream()
            .map(productionArea -> {
                ResponseOut responseOut = new ResponseOut();
                responseOut.id = productionArea.getId();
                responseOut.name = productionArea.getName();
                responseOut.code = productionArea.getCode();
                responseOut.hasEmployee = !productionArea.getEmployeeList().isEmpty();
                responseOut.hasDefect = !productionArea.getProductionDefectList().isEmpty();
                responseOut.technological = productionArea.isTechnological();
                return responseOut;
            }).collect(Collectors.toList());
    }

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditProductionAreaForm form) {
        ProductionArea productionArea = form.getId() != null ? productionAreaService.read(form.getId()) : new ProductionArea();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            if (productionAreaService.existsByCodeAndIdNot(form.getCode(), form.getId())) {
                throw new AlertUIException("Участок под указанным кодом уже существует");
            }
            productionArea.setName(form.getName().trim());
            productionArea.setCode(form.getCode());
            productionArea.setTechnological(form.isTechnological());
            productionAreaService.save(productionArea);
        }
        return response;
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        productionAreaService.deleteById(id);
    }

    // Загрузка списка работников
    @GetMapping("/list/info/employee/load")
    public List<?> list_info_employee_load(Long areaId) {
        @Getter
        class ResponseOut {
            long id; // идентификатор
            Integer personnelNumber; // табельный номер
            String name; // ФИО
        }
        return productionAreaService.read(areaId).getEmployeeList().stream()
            .map(employee -> {
                ResponseOut responseOut = new ResponseOut();
                responseOut.id = employee.getId();
                responseOut.personnelNumber = employee.getPersonnelNumber();
                responseOut.name = employee.getSecondName() + " " + employee.getFirstName();
                return responseOut;
            }).collect(Collectors.toList());
    }

    // Загрузка списка производственных дефектов
    @GetMapping("/list/info/defect/load")
    public List<?> list_info_defect_load(Long areaId) {
        @Getter
        class ResponseOut {
            long id; // идентификатор
            Integer code; // код
            String description; // комментарий
        }
        return productionAreaService.read(areaId).getProductionDefectList().stream()
            .map(employee -> {
                ResponseOut responseOut = new ResponseOut();
                responseOut.id = employee.getId();
                responseOut.code = employee.getCode();
                responseOut.description = employee.getDescription();
                return responseOut;
            }).collect(Collectors.toList());
    }

    // Загрузка списка производственных кладовок
    @GetMapping("/list/info/storeroom/load")
    public List<?> list_info_storeroom_load(Long areaId) {
        @Getter
        class ResponseOut {
            long id; // идентификатор
            String code; // код
            String name; // наименование
        }
        return Collections.emptyList();/*productionAreaService.read(areaId).getProductionStoreList().stream()
            .map(productionStoreroom -> {
                ResponseOut responseOut = new ResponseOut();
                responseOut.id = productionStoreroom.getId();
                responseOut.code = productionStoreroom.getCode();
                responseOut.name = productionStoreroom.getName();
                return responseOut;
            }).collect(Collectors.toList());*/
    }
}