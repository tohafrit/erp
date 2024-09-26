package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.korundm.enumeration.TripStatus;
import ru.korundm.enumeration.TripType;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "trips")
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор командировки

    @Convert(converter = TripType.CustomConverter.class)
    @Column(name = "type", nullable = false)
    private TripType type; // тип командировки

    @Column(name = "name", nullable = false)
    private String name; // название командировки

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee; // сотрудник

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chief_id", nullable = false)
    private User chief; // начальник

    @Column(name = "date_on", nullable = false)
    private LocalDate date; // дата командировки

    @Column(name = "time_from", nullable = false)
    private LocalTime timeFrom; // время с

    @Column(name = "time_to", nullable = false)
    private LocalTime timeTo; // время по

    @Convert(converter = TripStatus.CustomConverter.class)
    @Column(name = "status", nullable = false)
    private TripStatus status; // статус командировки

    @Column(name = "date_end_on")
    private LocalDate dateTo; // дата окончания командировки
}