package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ComponentGroup;

public interface ComponentGroupRepository extends JpaRepository<ComponentGroup, Long> {}