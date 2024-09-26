package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ComponentKind;

public interface ComponentKindRepository extends JpaRepository<ComponentKind, Long> {}