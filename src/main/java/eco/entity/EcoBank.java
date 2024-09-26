package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.constant.BaseConstant;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы ECOPLAN.BANK
 * @author zhestkov_an
 * Date:   30.09.2019
 */
@Entity
@Table(name = "BANK")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoBank implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @Column(name = "code_1c")
    private String code1C; // код в базе 1С

    @Column(name = "name")
    private String name; // наименование

    @Column(name = "location")
    private String location; // местонахождения

    @Column(name = "bik")
    private String bik; // бик

    @Column(name = "corr_account")
    private String correspondentAccount; // корр.счет

    @Column(name = "address")
    private String address; // адрес

    @Column(name = "phone")
    private String phone; // телефон

    @OneToMany(mappedBy = "bank")
    @JsonIgnore
    @org.hibernate.annotations.Where(clause = "company_id = " + BaseConstant.ECO_MAIN_PLANT_ID)
    private List<EcoAccount> accountList = new ArrayList<>();
}