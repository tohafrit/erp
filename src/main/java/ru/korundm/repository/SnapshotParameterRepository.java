package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.SnapshotParameter;

public interface SnapshotParameterRepository extends JpaRepository<SnapshotParameter, Long> {}