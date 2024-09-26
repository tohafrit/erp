package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.entity.Product;
import ru.korundm.entity.User;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;
import ru.korundm.constant.BaseConstant;

import java.time.LocalDateTime;

/**
 * Форма добавления/редактирования пользовательского комментария к изделию
 */
@Getter
@Setter
@ToString
public class EditProductCommentForm implements Validatable {

    private Long id; // идентификатор
    @DateTimeFormat(pattern = BaseConstant.DATE_TIME_PATTERN)
    private LocalDateTime createdDate; // дата создания комментария к изделию
    private User createdBy; // кем создан коментарий
    private String comment; // комментарий
    private Product product; // изделие

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(comment) || comment.length() > 1024) {
            errors.putError("comment", ValidatorMsg.RANGE_LENGTH, 1, 1024);
        }
    }
}