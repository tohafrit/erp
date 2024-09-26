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
 * Сущность с описанием таблицы T_BIC_STATUS
 * @author mazur_ea
 * Date:   20.11.2019
 */
@Entity
@Table(name = "T_BIC_STATUS")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoTBicStatus implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "descr")
    private String description;
}