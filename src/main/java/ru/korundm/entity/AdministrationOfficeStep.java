package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.AdministrationOfficeStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "administration_office_steps")
@ToString
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class AdministrationOfficeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand_id")
    private AdministrationOfficeDemand demand; // заявка

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_id", nullable = false)
    private User executor; // пользователь-исполнитель

    @Convert(converter = AdministrationOfficeStatus.CustomConverter.class)
    @Column(name = "status", nullable = false)
    private AdministrationOfficeStatus status; // статус выполнения

    @Column(name = "note")
    private String note; // заключение

    @Column(name = "time", nullable = false)
    private LocalDateTime time; // время исполнения
}