package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.BomAttribute;

import java.util.List;

public interface BomAttributeRepository extends JpaRepository<BomAttribute, Long> {

    List<BomAttribute> findAllByBomId(Long bomId);

    BomAttribute findTopByLaunchIdAndBomId(long launchId, long bomId);
}