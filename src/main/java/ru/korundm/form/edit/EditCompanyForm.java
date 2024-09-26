package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.enumeration.CompanyTypeEnum;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class EditCompanyForm implements Validatable {

    private Long id; // идентификатор
    private String name; // наименование
    private String shortName; // короткое наименование
    private String fullName; // полное наименование
    private String chiefName; // ФИО руководителя
    private String chiefPosition; // должность руководителя
    private String phoneNumber; // телефон/факс
    private String contactPerson; // контактные лица
    private String inn; // ИНН
    private String kpp; // КПП
    private String ogrn; // ОГРН
    private String inspectorName; // название ПЗ
    private String inspectorHead; // руководитель ПЗ
    private String location; // местонахождение
    private String note; // комментарий
    private String factualAddress; // фактический адрес
    private String juridicalAddress; // юридический адрес
    private String mailAddress; // почтовый адрес

    private List<CompanyTypeEnum> companyTypeList = new ArrayList<>();

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(name) || name.length() > 128) {
            errors.putError("name", ValidatorMsg.RANGE_LENGTH, 1, 128);
        }
        if (fullName.length() > 256) {
            errors.putError("fullName", ValidatorMsg.RANGE_LENGTH, 0, 256);
        }
        if (chiefName.length() > 128) {
            errors.putError("chiefName", ValidatorMsg.RANGE_LENGTH, 0, 128);
        }
        if (chiefPosition.length() > 256) {
            errors.putError("chiefPosition", ValidatorMsg.RANGE_LENGTH, 0, 256);
        }
        if (phoneNumber.length() > 256) {
            errors.putError("phoneNumber", ValidatorMsg.RANGE_LENGTH, 0, 256);
        }
        if (contactPerson.length() > 256) {
            errors.putError("contactPerson", ValidatorMsg.RANGE_LENGTH, 0, 256);
        }
        if (inn.length() > 16) {
            errors.putError("inn", ValidatorMsg.RANGE_LENGTH, 0, 16);
        }
        if (kpp.length() > 16) {
            errors.putError("kpp", ValidatorMsg.RANGE_LENGTH, 0, 16);
        }
        if (ogrn.length() > 16) {
            errors.putError("ogrn", ValidatorMsg.RANGE_LENGTH, 0, 16);
        }
        if (inspectorName.length() > 128) {
            errors.putError("inspectorName", ValidatorMsg.RANGE_LENGTH, 0, 128);
        }
        if (inspectorHead.length() > 128) {
            errors.putError("inspectorHead", ValidatorMsg.RANGE_LENGTH, 0, 128);
        }
        if (location.length() > 128) {
            errors.putError("location", ValidatorMsg.RANGE_LENGTH, 0, 128);
        }
        if (note.length() > 1024) {
            errors.putError("note", ValidatorMsg.RANGE_LENGTH, 0, 1024);
        }
        if (factualAddress.length() > 256) {
            errors.putError("factualAddress", ValidatorMsg.RANGE_LENGTH, 0, 256);
        }
        if (juridicalAddress.length() > 256) {
            errors.putError("juridicalAddress", ValidatorMsg.RANGE_LENGTH, 0, 256);
        }
        if (mailAddress.length() > 256) {
            errors.putError("mailAddress", ValidatorMsg.RANGE_LENGTH, 0, 256);
        }
        if (CollectionUtils.isEmpty(companyTypeList)) {
            errors.putError("companyTypeList", ValidatorMsg.REQUIRED);
        }
    }
}