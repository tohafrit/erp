package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {}