package eco.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.ContractType;
import ru.korundm.enumeration.Performer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы CONTRACT
 * @author zhestkov_an
 * Date:   29.08.2019
 */
@Entity
@Table(name = "CONTRACT")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoContract implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @Column(name = "code_1c")
    private String code1C; // код в 1с

    @Column(name = "contract_type")
    private Long contractType; // тип контракта (enum ContractType.java)

    @Column(name = "contract_number")
    private Long contractNumber; // порядковый номер договора в течение года

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoCompany customer; // организация-заказчик

    @Column(name = "external_name")
    private String externalName; // внешний номер договора

    @Column(name = "note")
    private String note; // комментарий

    @Column(name = "last_addendum_number")
    private Long lastAddendumNumber; //

    @Column(name = "reference_number")
    private String referenceNumber; //

    @Column(name = "performer")
    private Long performer; // организация-исполнитель (enum Performer.java)

    @Column(name = "chief_designer_id")
    private Integer chiefDesigner; // ведущий (не используется с 2011) CHIEF_DESIGNER_ID

    @Column(name = "requester_id")
    private Integer requester; // создатель договора

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    @JsonIgnore
    private EcoUserInfo manager; // ведущий договор

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    @OrderBy("id asc")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoContractSection> sectionList = new ArrayList<>(); // секции контракта

    /**
     * Метод для получения полного номера контракта
     * @return полный номер контракта
     */
    public String getFullNumber() {
        StringBuilder fullNumber = new StringBuilder();
        fullNumber.append(getContractNumber()).append("/");
        fullNumber.append(Performer.Companion.getById(getPerformer()).getPrefix()).append("-");
        fullNumber.append(ContractType.Companion.getById(getContractType()).getCode()).append("-");
        int year = getSectionList().get(0).getDate().getYear();
        fullNumber.append(Integer.toString(year).substring(2));
        return fullNumber.toString();
    }

    /**
     * Метод для получения внешнего или полного номера контракта
     * @return внешний номер, если заполнен, или полный номер контракта
     */
    public String getExternalNameOrFullNumber() {
        return externalName == null ? getFullNumber() : externalName;
    }

    /**
     * Метод определяет активность договора
     * @return true - активный договор, если false - то архивный договор
     */
    public boolean isContractActive() {
        return getSectionList().stream().anyMatch(ecoContractSection -> ecoContractSection.getArchiveDate() == null);
    }
}