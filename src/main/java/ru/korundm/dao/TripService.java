package ru.korundm.dao;

import ru.korundm.entity.Trip;
import ru.korundm.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface TripService extends CommonService<Trip> {

    List<Trip> readAll(List<Long> tripList);

    List<Trip> getByConfirmation(User employee, LocalDate date, Boolean confirmation);

    List<Trip> getByEmployee(User employee, LocalDate date);

    List<Trip> getByChief(User chief, LocalDate date);

    boolean existByChief(User chief, LocalDate date);

    List<Trip> getByDate(LocalDate startDate, LocalDate endDate);
}
