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
 * Сущность с описанием таблицы UNIT
 * @author pakhunov_an
 * Date:   24.07.2020
 */
@Entity
@Table(name = "UNIT")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoUnit implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @Column(name = "name")
    private String name;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "okei")
    private String okei;
}