package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Producer;

public interface ProducerRepository extends JpaRepository<Producer, Long> {}