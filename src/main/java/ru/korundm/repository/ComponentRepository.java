package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Component;

public interface ComponentRepository extends JpaRepository<Component, Long> {

    Component findFirstByPosition(Integer position);
}