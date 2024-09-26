package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.korundm.entity.CorporateDocumentCategory;

import java.util.List;

public interface CorporateDocumentCategoryRepository extends JpaRepository<CorporateDocumentCategory, Long> {

    List<CorporateDocumentCategory> findAllByParentIsNull();

    @Query(nativeQuery = true, value = "" +
        "WITH RECURSIVE corporate_document_categories_rec(id) AS (\n" +
        "  SELECT :parentId\n" +
        "\n" +
        "  UNION ALL\n" +
        "\n" +
        "  SELECT\n" +
        "    cdc.id\n" +
        "  FROM\n" +
        "    corporate_document_categories cdc\n" +
        "    JOIN\n" +
        "    corporate_document_categories rec\n" +
        "    ON\n" +
        "    rec.id = cdc.parent_id\n" +
        ")\n" +
        "SELECT id FROM corporate_document_categories_rec WHERE id IS NOT NULL"
    )
    List<Long> findAllSiblingsIdByParentId(@Param("parentId") Long parentId);
}