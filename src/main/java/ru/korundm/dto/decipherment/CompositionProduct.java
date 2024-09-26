package ru.korundm.dto.decipherment;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.BaseConstant;

import java.io.Serializable;

/**
 * Класс для хранения модели данных для работы с изделием в определении состава расшифровки
 * @author mazur_ea
 * Date:   29.08.2019
 */
@Getter @Setter
@EqualsAndHashCode(of = "fullHierarchyNumber")
public final class CompositionProduct implements Serializable {

    /**
     * Уникальный номер изделия состава в иерархии - состоит и строки типа ...{parent_specificationId}-{parent_productId}-{parent_versionId}_{specificationId}-{productId}-{versionId}.
     * Номер похож на адрес каждой папки в проводнике windows.
     * Необходим для определения положения изделия в иерархии состава, поскольку других идентификаторов способных это делать не имеется.
     * По некоторым подсчетам имеется 4 уровня вложенности.
     */
    private String fullHierarchyNumber;
    private Long specificationId; // идентификатор спецификации изделия
    private Long productId; // идентификатор изделия
    private Long versionId; // идентификатор версии изделия (bomId)

    private Long selectedVersionId; // идентификатор выбранной версии для изделия
    private String selectedVersion; // наименования выбранной версия изделия

    /**
     * Метод получения номера изделия-предка в иерархии состава
     * @return номер изделия-предка в иерархии состава
     */
    public String getParentFullHierarchyNumber() {
        String parentFullHierarchyNumber = "";
        if (StringUtils.isNotBlank(fullHierarchyNumber) && fullHierarchyNumber.contains(BaseConstant.UNDERSCORE)) {
            parentFullHierarchyNumber = fullHierarchyNumber.substring(0, fullHierarchyNumber.lastIndexOf(BaseConstant.UNDERSCORE));
        }
        return parentFullHierarchyNumber;
    }

    public String getFullHierarchyNumber() {
        return fullHierarchyNumber;
    }

    public Long getSpecificationId() {
        return specificationId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public Long getSelectedVersionId() {
        return selectedVersionId;
    }

    public String getSelectedVersion() {
        return selectedVersion;
    }
}