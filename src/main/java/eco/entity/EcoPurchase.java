package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы PROC_PARAM
 * @author pakhunov_an
 * Date:   19.08.2019
 */
@Entity
@Table(name = "PROC_PARAM")
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoPurchase implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @Size(min = 1, message = "{common.hibernate.required}")
    @Column(name = "name", nullable = false)
    private String name; // наименование

    @NotNull
    @Column(name = "plan_date")
    private LocalDate planDate; // плановый срок

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launch_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private EcoLaunch launch; // запуск

    @Column(name = "bom_selection", nullable = false)
    private Long type; // тип версий ЗС изделий (BomType.java)

    @Column(name = "note")
    private String note; // комментарий

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoUserInfo user;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "PROC_PARAM_PRIOR_LAUNCH",
        joinColumns = @JoinColumn(name = "proc_param_id"),
        inverseJoinColumns = @JoinColumn(name = "launch_id")
    )
    @JsonIgnore
    private List<EcoLaunch> purchaseLaunchList = new ArrayList<>(); // список запусков
}