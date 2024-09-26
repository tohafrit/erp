package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Trip;
import ru.korundm.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByEmployeeAndDateGreaterThanEqualOrderByDateAsc(User employee, LocalDate date);

    List<Trip> findByEmployeeAndDateGreaterThanEqualAndStatusEqualsOrderByDateAsc(User employee, LocalDate date, Boolean status);

    List<Trip> findByChiefAndDateGreaterThanEqualOrderByDateAsc(User chief, LocalDate date);

    boolean existsByChiefAndDateGreaterThanEqual(User chief, LocalDate date);

    List<Trip> findByDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(LocalDate startDate, LocalDate endDate);
}