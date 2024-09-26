package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.IndexForecast;

public interface IndexForecastRepository extends JpaRepository<IndexForecast, Long> {}