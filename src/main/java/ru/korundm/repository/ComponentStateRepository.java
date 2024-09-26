package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ComponentState;

public interface ComponentStateRepository extends JpaRepository<ComponentState, Long> {
    
    ComponentState getFirstByCode(String code);
}