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
 * Сущность с описанием таблицы T_BOM_COMPONENT_CATEGORY
 * @author mazur_ea
 * Date:   12.11.2019
 */
@Entity
@Table(name = "T_BOM_COMPONENT_CATEGORY")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoBomComponentCategory implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "order_id")
    private Integer orderId;
}