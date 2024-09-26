package eco.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы типов изделий
 * @author zhestkov_an
 * Date:   29.08.2019
 */
@Entity
@Table(name = "T_PRODUCT_TYPE")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoProductType implements Serializable {

    @Id
    @Column(name = "type_id")
    private Long id; // идентификатор

    @Column(name = "type")
    private String name; // наименование

    @Column(name = "type_descr")
    private String description; // описание

    @Column(name = "order_index")
    private Long orderIndex;

    @OneToMany(mappedBy = "productType", cascade = CascadeType.ALL)
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoProduct> productList = new ArrayList<>();
}