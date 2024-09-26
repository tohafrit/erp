package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ComponentAttributeValue;

import java.util.List;

public interface ComponentAttributeValueRepository extends JpaRepository<ComponentAttributeValue, Long> {

    List<ComponentAttributeValue> findAllByAttributeIdIn(List<Long> idList);
}