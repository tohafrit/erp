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
 * Сущность с описанием таблицы ECOPLAN.T_1C_USER
 * @author berezin_mm
 * Date:   31.01.2020
 */
@Entity
@Table(name = "T_1C_USER")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class Eco1CUser implements Serializable {

    @Id
    @Column(name = "user_id")
    private Long id; // id

    @Column(name = "user_descr")
    private String name; // имя пользователя

    @OneToMany(mappedBy = "bookkeeper")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoInOutDocument> inOutDocumentListByBookkeeper = new ArrayList<>(); // список накладных, относящихся к бухгалтеру

    @OneToMany(mappedBy = "shipper")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoInOutDocument> inOutDocumentListByShipper = new ArrayList<>(); // список накладных, относящихся к произведшему отпуск

    @OneToMany(mappedBy = "permitter")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoInOutDocument> inOutDocumentListByPermitter = new ArrayList<>(); // список накладных, относящихся к разрешившему отпуск
}
