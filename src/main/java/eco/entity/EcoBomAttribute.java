package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Сущность с описанием таблицы ECOPLAN.BOM_ATTRIBUTE
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "BOM_ATTRIBUTE")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoBomAttribute implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launch_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoLaunch launch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoBom bom;

    @Column(name = "approve_date")
    private LocalDate approveDate;

    @Column(name = "approve_user")
    private String approveUser;

    @Column(name = "accept_date")
    private LocalDate acceptDate;

    @Column(name = "accept_user")
    private String acceptUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document", referencedColumnName = "id")
    @JsonIgnore
    private EcoDocument document; // документ

    @Column(name = "purspec_date")
    private LocalDate purspecDate;

    @Column(name = "purspec_user")
    private String purspecUser;

    @Column(name = "purspec_contractors")
    private Long purspecContractors;

    @Column(name = "prodstaff_date")
    private LocalDate prodstaffDate;

    @Column(name = "prodstaff_user")
    private String prodstaffUser;

    @Column(name = "prodstaff_contractor")
    private Long prodstaffContractor;
}