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
 * Сущность с описанием таблицы LABOUR
 * @author mazur_ea
 * Date:   28.11.2019
 */
@Entity
@Table(name = "LABOUR")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoLabour implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // идентификатор

    @Column(name = "labour_name")
    private String labourName; // вид работы

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private EcoCompany company; // исполнитель работы

    @Column(name = "subtraction")
    private Long subtraction; // вычеты для расчёта Без упаковки. 1 - вычитать

    @OneToMany(mappedBy = "labour")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoProductLabourReference> productLabourReferenceList = new ArrayList<>();
}