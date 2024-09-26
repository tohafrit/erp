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
 * Сущность с описанием таблицы LABOUR_PRICE
 * @author mazur_ea
 * Date:   28.11.2019
 */
@Entity
@Table(name = "LABOUR_PRICE")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoLabourPrice implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labour_id")
    private EcoLabour labour; // вид работы

    @Column(name = "hourly_pay")
    private Double hourlyPay; // норма оплаты

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_id")
    private EcoLabourProtocol protocol; // протокол расценки

    @Column(name = "order_index")
    private Long orderIndex; // для сортировки

    @OneToMany(mappedBy = "labourPrice")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoProductLabourReference> productLabourReferenceList = new ArrayList<>();
}