package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.korundm.util.KtCommonUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы пользователей
 * @author pakhunov_an
 * Date:   06.02.2018
 */
@Entity
@Table(name = "users")
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class User implements Serializable {

    public User() {}

    public User(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "active", nullable = false)
    private boolean active; // активность

    @Column(name = "username", length = 32, nullable = false, unique = true, updatable = false)
    private String userName; // логин/уникальное имя пользователя

    @Column(name = "first_name", length = 32, nullable = false)
    private String firstName; // имя

    @Column(name = "middle_name", length = 32)
    private String middleName; // отчество

    @Column(name = "last_name", length = 32, nullable = false)
    private String lastName; // фамилия

    @Column(name = "email")
    private String email; // e-mail сотрудника

    @OneToMany(mappedBy = "employee")
    private List<Trip> usersTripList = new ArrayList<>(); // список командировок пользователя

    @OneToMany(mappedBy = "chief")
    private List<Trip> subordinateTripList = new ArrayList<>(); // список командировок, одобренных пользователем

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roleList = new ArrayList<>(); // список ролей

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_privileges",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    private List<Privilege> privilegeList = new ArrayList<>(); // список доступов

    @ManyToMany(mappedBy = "userList", cascade = CascadeType.ALL)
    private List<Printer> printerList = new ArrayList<>(); // список принтеров

    /**
     * Метод для получения ФИО в виде - Фамилия И.О.
     * @return Фамилия И.О.
     */
    public String getUserOfficialName() {
        return KtCommonUtil.INSTANCE.userFullName(lastName, firstName, middleName);
    }

    /**
     * Метод для получения ФИО в виде - И.О. Фамилия
     * @return И.О. Фамилия
     */
    public String getUserShortName() {
        return KtCommonUtil.INSTANCE.userShortName(lastName, firstName, middleName);
    }

    /**
     * Метод для получения ФИО в виде - Фамилия Имя Отчество
     * @return Фамилия Имя Отчество
     */
    public String getFullName() {
        return KtCommonUtil.INSTANCE.userFullName(lastName, firstName, middleName, false);
    }

    public Long getId() {
        return this.id;
    }
}