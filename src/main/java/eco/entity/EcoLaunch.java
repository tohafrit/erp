package eco.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы запусков LAUNCH
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "LAUNCH")
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoLaunch implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // идентификатор

    @Column(name = "year")
    private LocalDate year; // год запуска

    @Column(name = "number_in_year")
    private Integer numberInYear; // номер запуска в текущем году

    @Column(name = "note")
    private String note; // комментарий

    @Column(name = "confirm_date")
    private LocalDate confirmDate; // дата утверждения запуска

    @OneToMany(mappedBy = "launch")
    private List<EcoBomAttribute> bomAttributeList = new ArrayList<>();

    @OneToMany(mappedBy = "launch")
    private List<EcoSnapshot> snapshotList = new ArrayList<>();

    @OneToMany(mappedBy = "launch")
    private List<EcoLaunchProduct> launchProductList = new ArrayList<>();

    @ManyToMany(mappedBy = "purchaseLaunchList", cascade = CascadeType.ALL)
    private List<EcoPurchase> launchPurchaseList = new ArrayList<>(); // список сливов

    /**
     * Метод для получения полного номера запуска
     * @return полный номер запуска
     */
    public String getFullNumber() {
        return getNumberInYear() + "/" + Integer.toString(getYear().getYear()).substring(2);
    }
}