package ru.korundm.controller.action.prod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.ProductionAreaService;
import ru.korundm.dao.TechnologicalToolService;
import ru.korundm.entity.TechnologicalTool;
import ru.korundm.form.edit.EditTechnologicalToolForm;
import ru.korundm.form.search.TechnologicalToolFilterForm;
import ru.korundm.helper.ValidatorResponse;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Prod.TECHNOLOGICAL_TOOL)
@SessionAttributes(names = "technologicalToolListFilterForm", types = TechnologicalToolFilterForm.class)
public class TechnologicalToolActionProdController {

    @Resource
    private MessageSource messageSource;

    private static final Locale locale = LocaleContextHolder.getLocale();
    private static final String TECHNOLOGICAL_TOOL_LIST_FILTER_FORM_ATTR = "technologicalToolListFilterForm";

    private final TechnologicalToolService technologicalToolService;
    private final ProductionAreaService productionAreaService;
    private final ObjectMapper jsonMapper;

    public TechnologicalToolActionProdController(
        TechnologicalToolService technologicalToolService,
        ProductionAreaService productionAreaService,
        ObjectMapper jsonMapper
    ) {
        this.technologicalToolService = technologicalToolService;
        this.productionAreaService = productionAreaService;
        this.jsonMapper = jsonMapper;
    }

    // Загрузка списка
    @GetMapping("/list/load")
    public List<?> list_load(
        ModelMap model,
        String filterForm
    ) throws JsonProcessingException {
        @Getter
        class ResponseOut {
            Long id; // идентификатор
            String sign; // обозначение
            String name; // наименование
            String type; // тип
            String appointment; // назначение
            String link; // ссылка на файл
            String state; // состояние
            String productionArea; // участок
            LocalDate issueDate; // дата выпуска
            String user; // кем выпущен
        }
        TechnologicalToolFilterForm form = jsonMapper.readValue(filterForm, TechnologicalToolFilterForm.class);
        model.addAttribute(TECHNOLOGICAL_TOOL_LIST_FILTER_FORM_ATTR, form);
        return technologicalToolService.getAllByForm(form).stream()
            .map(technologicalTool -> {
                ResponseOut responseOut = new ResponseOut();
                responseOut.id = technologicalTool.getId();
                responseOut.sign = technologicalTool.getSign();
                responseOut.name = technologicalTool.getName();
                responseOut.type = messageSource.getMessage(technologicalTool.getType().getProperty(), null, locale);
                responseOut.appointment = technologicalTool.getAppointment();
                responseOut.link = technologicalTool.getLink();
                responseOut.state = technologicalTool.getState();
                responseOut.productionArea = technologicalTool.getProductionAreaList().stream()
                    .map(tool -> tool.getFormatCode() + " " + tool.getName()).collect(Collectors.joining(", "));
                responseOut.issueDate = technologicalTool.getIssueDate();
                responseOut.user = technologicalTool.getUser() != null ? technologicalTool.getUser().getUserOfficialName() : "";
                return responseOut;
            }).collect(Collectors.toList());
    }

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditTechnologicalToolForm form) {
        Long formId = form.getId();
        TechnologicalTool technologicalTool = formId != null ? technologicalToolService.read(formId) : new TechnologicalTool();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            technologicalTool.setSign(form.getSign());
            technologicalTool.setName(form.getName());
            technologicalTool.setAppointment(form.getAppointment());
            technologicalTool.setLink(form.getLink());
            technologicalTool.setState(form.getState());
            technologicalTool.setIssueDate(form.getIssueDate());
            technologicalTool.setUser(form.getUser());
            technologicalTool.setType(form.getType());
            technologicalTool.setProductionAreaList(productionAreaService.getAllById(form.getProductionAreaIdList()));
            technologicalToolService.save(technologicalTool);
            if (formId == null) {
                response.putAttribute("addedTechnologicalToolId", technologicalTool.getId());
            }
        }
        return response;
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        technologicalToolService.deleteById(id);
    }
}