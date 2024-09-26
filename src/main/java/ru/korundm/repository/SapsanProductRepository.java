package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.SapsanProduct;

public interface SapsanProductRepository extends JpaRepository<SapsanProduct, Long> {

    SapsanProduct findByPrefix(String prefix);
}