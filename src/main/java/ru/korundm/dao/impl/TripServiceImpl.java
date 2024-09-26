package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.TripService;
import ru.korundm.entity.Trip;
import ru.korundm.entity.User;
import ru.korundm.repository.TripRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public List<Trip> getAll() {
        return tripRepository.findAll();
    }

    @Override
    public List<Trip> getAllById(List<Long> idList) {
        return tripRepository.findAllById(idList);
    }

    @Override
    public Trip save(Trip object) {
        return tripRepository.save(object);
    }

    @Override
    public Trip read(long id) {
        return tripRepository.getOne(id);
    }

    @Override
    public void delete(Trip object) {
        tripRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        tripRepository.deleteById(id);
    }

    @Override
    public List<Trip> saveAll(List<Trip> list) {
        return tripRepository.saveAll(list);
    }

    @Override
    public List<Trip> readAll(List<Long> tripList) {
        return tripRepository.findAllById(tripList);
    }

    public List<Trip> getByEmployee(User employee, LocalDate date) {
        return tripRepository.findByEmployeeAndDateGreaterThanEqualOrderByDateAsc(employee, date);
    }

    public List<Trip> getByConfirmation(User employee, LocalDate date, Boolean confirmation) {
        return tripRepository.findByEmployeeAndDateGreaterThanEqualAndStatusEqualsOrderByDateAsc(employee, date, confirmation);
    }

    public List<Trip> getByChief(User chief, LocalDate date) {
        return tripRepository.findByChiefAndDateGreaterThanEqualOrderByDateAsc(chief, date);
    }

    public boolean existByChief(User chief, LocalDate date) {
        return tripRepository.existsByChiefAndDateGreaterThanEqual(chief, date);
    }

    public List<Trip> getByDate(LocalDate startDate, LocalDate endDate) {
        return tripRepository.findByDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(startDate, endDate);
    }
}