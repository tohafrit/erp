package eco.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы сапсановких изделий
 * @author pakhunov_an
 * Date:   23.08.2019
 */
@Entity
@Table(name = "SAPSAN_PRODUCT")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoSapsanProduct implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "prefix")
    private String prefix;

    @OneToMany(mappedBy = "sapsanProduct")
    private List<EcoSapsanProductBom> sapsanProductBomList = new ArrayList<>();
}