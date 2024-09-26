package ru.korundm.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Сущность для промежуточного хранения элемента структуры состава изделия
 * @author mazur_ea
 * Date:   18.05.2020
 */
@Getter
@Setter
@ToString
public final class ProductStructureItem implements Serializable {

    private Long id;
    private Long productId;
    private String conditionalName; // условное наименование
    private int quantity; // количество
    private String producer; // Изготовитель
    private boolean main; // основной элемент иерархии
    @JsonProperty("_children")
    private List<ProductStructureItem> childList;
}