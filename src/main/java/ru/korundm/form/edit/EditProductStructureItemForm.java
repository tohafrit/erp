package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.entity.Company;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

/**
 * Форма редактирования элемента состава изделия
 * @author pakhunov_an
 * Date:   12.06.2020
 */
@Getter
@Setter
@ToString
public class EditProductStructureItemForm implements Validatable {

    private Long id;
    private long lockVersion;
    private Integer quantity; // количество
    private Company producer; // изготовитель

    @Override
    public void validate(ValidatorErrors errors) {
        if (quantity == null || quantity == 0 || quantity > 255) {
            errors.putError("quantity", "validator.form.fixRange", 1, 255);
        }
    }
}