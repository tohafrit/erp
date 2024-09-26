package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.MessageHistory;

public interface MessageHistoryRepository extends JpaRepository<MessageHistory, Long> {}