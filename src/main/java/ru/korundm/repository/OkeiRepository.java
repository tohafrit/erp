package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Okei;

public interface OkeiRepository extends JpaRepository<Okei, Long> {

    Okei findFirstByCode(String code);
}