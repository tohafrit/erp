package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.PrivilegeGroup;

public interface PrivilegeGroupRepository extends JpaRepository<PrivilegeGroup, Long> {}