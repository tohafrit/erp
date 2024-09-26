package eco.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы изделий в запуске
 * @author pakhunov_an
 * Date:   26.08.2019
 */
@Entity
@Table(name = "LAUNCH_PRODUCT")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoLaunchProduct implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // идентификатор

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launch_id", nullable = false)
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoLaunch launch; // запуск

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoProduct product; // изделие

    @Column(name = "amount", nullable = false)
    private Long amount; // кол-во изделий <По договору> + <Б1> + <В задел>. Не учитывается кол-во <В составе других изделий>

    @Column(name = "reserve", nullable = false)
    private Long reserve; // кол-во изделий <В Задел>

    @Column(name = "note")
    private String note; // комментарий

    @Column(name = "order_index")
    private Long orderIndex; // для упорядочивания

    @Column(name = "for_contract")
    private Long forContract; // количество по договору. Сумма Эллотментов, смотрящих на этот LP

    @OneToMany(mappedBy = "launchProduct",cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonBackReference
    private List<EcoAllotment> allotmentList = new ArrayList<>();

    @OneToMany(mappedBy = "launchProduct")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonBackReference
    private List<EcoProductionLot> productionLotList = new ArrayList<>();

    @OneToMany(mappedBy = "launchProduct")
    @JsonIgnore
    private List<EcoLaunchProductProductionLotReference> launchProductProductionLotReferenceList = new ArrayList<>();

    @org.hibernate.annotations.Formula("amount - reserve - for_contract")
    private Long unallotted; // нераспределенные (ECOPLAN.V_LP_UNALLOTED)
}