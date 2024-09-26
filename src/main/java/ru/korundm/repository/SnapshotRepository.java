package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Snapshot;

public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {}