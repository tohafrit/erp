package ru.korundm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.constant.BaseConstant;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы purchases (ex. PROC_PARAM)
 * @author pakhunov_an
 * Date:   19.08.2019
 */
@Entity
@Table(name = "purchases")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class Purchase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "purchase")
    @org.hibernate.annotations.GenericGenerator(name = "purchase", strategy = BaseConstant.GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @Size(min = 1, message = "{common.hibernate.required}")
    @Column(name = "name", nullable = false)
    private String name; // наименование

    @NotNull
    @Column(name = "plan_date")
    private LocalDate planDate; // плановый срок

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launch_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Launch launch; // запуск

    @Column(name = "type", nullable = false)
    private Long type; // тип версий ЗС изделий (BomType.java)

    @Column(name = "note")
    private String note; // комментарий

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "purchase_xref_launch",
        joinColumns = @JoinColumn(name = "purchase_id"),
        inverseJoinColumns = @JoinColumn(name = "launch_id")
    )
    @JsonIgnore
    private List<Launch> purchaseLaunchList = new ArrayList<>(); // список запусков
}
