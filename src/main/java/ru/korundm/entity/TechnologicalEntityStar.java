package ru.korundm.entity;

import ru.korundm.constant.ValidatorMsg;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сущность с описанием таблицы с информацией о примечаниях в технологической документации
 */
@Entity
@Table(name = "technological_entity_star")
public class TechnologicalEntityStar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Size(min = 1, message = ValidatorMsg.REQUIRED)
    @Column(name = "name", nullable = false)
    private String name; // наименование

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technological_entity_id")
    private TechnologicalEntity technologicalEntity; // технологическая документация

    @ManyToMany(targetEntity = Product.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "technological_entity_star_xref_product",
        joinColumns = @JoinColumn(name = "technological_entity_star_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> productList = new ArrayList<>(); // список изделий применяемости

    public String getApplicabilityNames() {
        return productList.stream().map(Product::getConditionalName).collect(Collectors.joining("; "));
    }
}
