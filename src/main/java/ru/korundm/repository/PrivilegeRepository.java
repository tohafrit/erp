package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {}