package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.AdministrationOfficeDemand;
import ru.korundm.entity.User;

import java.util.List;

public interface AdministrationOfficeDemandRepository extends JpaRepository<AdministrationOfficeDemand, Long> {

    List<AdministrationOfficeDemand> findAllByUser(User user);
}