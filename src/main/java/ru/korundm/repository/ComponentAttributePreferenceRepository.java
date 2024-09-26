package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ComponentAttributePreference;

import java.util.List;

public interface ComponentAttributePreferenceRepository extends JpaRepository<ComponentAttributePreference, Long> {

    List<ComponentAttributePreference> findAllByAttributeId(Long attributeId);

    List<ComponentAttributePreference> findAllByAttributeIdIn(List<Long> idList);
}