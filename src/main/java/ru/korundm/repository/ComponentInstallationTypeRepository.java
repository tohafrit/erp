package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ComponentInstallationType;

public interface ComponentInstallationTypeRepository extends JpaRepository<ComponentInstallationType, Long> {}