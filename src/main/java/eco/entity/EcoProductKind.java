package eco.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы разделов плана производства
 * @author mazur_ea
 * Date:   06.02.2020
 */
@Entity
@Table(name = "T_PRODUCT_KIND")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoProductKind implements Serializable {

    @Id
    @Column(name = "type_id")
    private Long id; // идентификатор

    @Column(name = "type")
    private String type; // наименование

    @Column(name = "type_descr")
    private String description; // описание

    @Column(name = "order_index")
    private Long orderIndex;
}