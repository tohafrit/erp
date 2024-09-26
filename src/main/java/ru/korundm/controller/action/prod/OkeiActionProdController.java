package ru.korundm.controller.action.prod;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.dao.OkeiService;
import ru.korundm.entity.Okei;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.constant.RequestPath;
import ru.korundm.util.CommonUtil;

import java.util.List;
import java.util.Objects;

@ActionController(RequestPath.Action.Prod.OKEI)
public class OkeiActionProdController {

    private final OkeiService okeiService;

    public OkeiActionProdController(
        OkeiService okeiService
    ) {
        this.okeiService = okeiService;
    }

    // Загрузка списка
    @GetMapping("/list/load")
    public List<Okei> list_load() {
        return okeiService.getAll();
    }

    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(Okei okei) {
        ValidatorResponse response = new ValidatorResponse();
        Okei exist = okeiService.getByCode(okei.getCode().trim());
        if (StringUtils.isBlank(okei.getName())) {
            response.putError("name", ValidatorMsg.REQUIRED);
        }
        if (StringUtils.isBlank(okei.getCode())) {
            response.putError("code", ValidatorMsg.REQUIRED);
        } else if (exist != null && !Objects.equals(exist.getId(), okei.getId())) {
            response.putError("code", ValidatorMsg.UNIQUE);
        }
        if (okei.getCoefficient() == null) {
            response.putError("coefficient", ValidatorMsg.REQUIRED);
        }
        if (StringUtils.isBlank(okei.getSymbolNational())) {
            response.putError("symbolNational", ValidatorMsg.REQUIRED);
        }
        if (StringUtils.isBlank(okei.getSymbolInternational())) {
            response.putError("symbolInternational", ValidatorMsg.REQUIRED);
        }
        if (StringUtils.isBlank(okei.getCodeLetterNational())) {
            response.putError("codeLetterNational", ValidatorMsg.REQUIRED);
        }
        if (StringUtils.isBlank(okei.getCodeLetterInternational())) {
            response.putError("codeLetterInternational", ValidatorMsg.REQUIRED);
        }
        if (response.isValid()) {
            okei.setCode(CommonUtil.formatZero(okei.getCode(), 3));
            okeiService.save(okei);
        }
        return response;
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        okeiService.deleteById(id);
    }
}