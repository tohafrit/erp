package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.korundm.entity.Documentation;

import java.util.List;

public interface DocumentationRepository extends JpaRepository<Documentation, Long> {

    List<Documentation> findAllByParentIsNull();

    @Query(nativeQuery = true, value = "" +
            "WITH RECURSIVE documentation_rec(id) AS (\n" +
            "  SELECT :parentId\n" +
            "\n" +
            "  UNION ALL\n" +
            "\n" +
            "  SELECT\n" +
            "    d.id\n" +
            "  FROM\n" +
            "    documentation d\n" +
            "    JOIN\n" +
            "    documentation_rec rec\n" +
            "    ON\n" +
            "    rec.id = d.parent_id\n" +
            ")\n" +
            "SELECT id FROM documentation_rec WHERE id IS NOT NULL"
    )
    List<Long> findAllSiblingsIdByParentId(@Param("parentId") Long parentId);

    Documentation findFirstByName(String name);
}