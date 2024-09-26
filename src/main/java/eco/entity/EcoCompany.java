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
 * Сущность с описанием таблицы COMPANY
 * @author zhestkov_an
 * Date:   30.08.2019
 */
@Entity
@Table(name = "COMPANY")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoCompany implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "postal_address")
    private String postalAddress; // почтовый адрес

    @Column(name = "phone_number")
    private String phoneNumber; // телефоны и факсы в свободной форме

    @Column(name = "note")
    private String note; // комментарий

    @Column(name = "location")
    private String location; // город (регион)

    @Column(name = "legal_address")
    private String legalAddress; // юридический адрес

    @Column(name = "kpp")
    private String kpp; // КПП

    @Column(name = "inspector_name")
    private String inspectorName; // наименование организации - представителя заказчика (ПЗ)

    @Column(name = "inspector_head")
    private String inspectorHead; // ФИО руководителя ПЗ

    @Column(name = "inn")
    private String inn; // ИНН

    @Column(name = "full_name")
    private String fullName; // полное наименование организации

    @Column(name = "economics_chief_position")
    private String economicsChiefPosition;

    @Column(name = "economics_chief_name")
    private String economicsChiefName;

    @Column(name = "doc_org_id")
    private Long docOrgID; // ссылка на компанию в схеме ECODOC

    @Column(name = "contact_person")
    private String contactPerson; // контактная информация лица в свободной форме

    @Column(name = "company_type")
    private Long companyType;

    @Column(name = "company_name", nullable = false)
    private String name; // наименование организации

    @Column(name = "code_1c")
    private String code1C; // код в базе 1С

    @Column(name = "chief_position")
    private String chiefPosition; // должность руководителя

    @Column(name = "chief_name")
    private String chiefName; // ФИО руководителя

    @OneToMany(mappedBy = "customer")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoContract> contractList = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoInOutDocument> inOutDocumentListByReceiver = new ArrayList<>(); // приходные документы

    @OneToMany(mappedBy = "outCorresp")
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoInOutDocument> inOutDocumentListByOutCorresp = new ArrayList<>(); // приходные документы
}