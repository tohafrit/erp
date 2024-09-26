package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.entity.FileStorage;
import ru.korundm.entity.ProductDocumentation;
import ru.korundm.helper.SingularFileStorableType;
import ru.korundm.helper.Validatable;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.util.FileStorageUtil;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Getter @Setter
public class EditProductDocumentationForm implements Validatable {

    private Long id; // идентификатор
    private Long productId; // изделие
    private String name; // наименование
    private String comment; // комментарий
    private FileStorage<ProductDocumentation, SingularFileStorableType> fileStorage; // файл
    private MultipartFile file; // файл

    @Override
    public void validate(@NotNull ValidatorErrors errors) {
        if (isBlank(name) || name.length() > 128) {
            errors.putError("name", ValidatorMsg.RANGE_LENGTH, 1, 128);
        }
        if (isNotBlank(comment) && comment.length() > 256) {
            errors.putError("comment", ValidatorMsg.RANGE_LENGTH, 0, 256);
        }
        FileStorageUtil.INSTANCE.validateFile(errors, fileStorage, file, "file", true);
    }
}