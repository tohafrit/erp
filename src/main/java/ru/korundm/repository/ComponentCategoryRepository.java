package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.korundm.entity.ComponentCategory;

import java.util.List;

public interface ComponentCategoryRepository extends JpaRepository<ComponentCategory, Long> {

    List<ComponentCategory> findAllByIdIsNotIn(List<Long> idList);

    List<ComponentCategory> findAllByParentIsNull();

    @Query(nativeQuery = true, value = "" +
        "WITH RECURSIVE component_categories_rec(id) AS (\n" +
        "  SELECT :parentId\n" +
        "\n" +
        "  UNION ALL\n" +
        "\n" +
        "  SELECT\n" +
        "    cc.id\n" +
        "  FROM\n" +
        "    component_categories cc\n" +
        "    JOIN\n" +
        "    component_categories_rec rec\n" +
        "    ON\n" +
        "    rec.id = cc.parent_id\n" +
        ")\n" +
        "SELECT id FROM component_categories_rec WHERE id IS NOT NULL"
    )
    List<Long> findAllSiblingsIdByParentId(@Param("parentId") Long parentId);
}