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
 * Сущность с описанием таблицы user_info
 * @author zhestkov_an
 * Date:   30.09.2019
 */
@Entity
@Table(name = "USER_INFO")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoUserInfo implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @Column(name = "user_name")
    private String userName; //

    @Column(name = "first_name")
    private String firstName; // имя

    @Column(name = "second_name")
    private String secondName; // отчество

    @Column(name = "last_name")
    private String lastName; // фамилия

    @Column(name = "email")
    private String email; // e-mail

    @Column(name = "name_with_initials")
    private String nameWithInitials; // фамилия с инициалами

    @Column(name = "note")
    private String note; // комментарий

    @Column(name = "active")
    private Boolean active; // активность записи

    @OneToMany(mappedBy = "constructor", cascade = CascadeType.ALL)
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<EcoProduct> productList = new ArrayList<>(); // список изделий

    // Геттеры и сеттеры  для корректного взаимодействия с котлин-классами

    public Long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }
}