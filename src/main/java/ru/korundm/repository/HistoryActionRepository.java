package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.HistoryAction;

public interface HistoryActionRepository extends JpaRepository<HistoryAction, Long> {}