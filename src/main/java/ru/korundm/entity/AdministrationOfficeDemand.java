package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "administration_office_demands")
@ToString
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class AdministrationOfficeDemand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate; // дата заявки

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // пользователь

    @Column(name = "room_number", nullable = false)
    private String roomNumber; // номер комнаты

    @Column(name = "reason", nullable = false)
    private String reason; // описание проблемы

    @OneToMany(mappedBy = "demand", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id desc")
    private List<AdministrationOfficeStep> stepList = new ArrayList<>(); // список этапов
}