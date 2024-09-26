package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.BomItemPosition;

import java.util.List;

public interface BomItemPositionRepository extends JpaRepository<BomItemPosition, Long> {

    List<BomItemPosition> findAllByBomItemId(Long bomItemId);

    long countAllByBomItemId(Long bomItemId);
}