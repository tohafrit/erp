package eco.repository;

import eco.entity.EcoProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoProductRepository extends JpaRepository<EcoProduct, Long> {

    List<EcoProduct> findAllByProductTypeIdIn(List<Long> idList);

    @Query(nativeQuery = true, value = "" +
        "WITH PRODUCTS(ID) AS (\n" +
        "  SELECT\n" +
        "    BSI.SUB_PRODUCT_ID\n" +
        "  FROM\n" +
        "    BOM B\n" +
        "    JOIN\n" +
        "    BOM_SPEC_ITEM BSI\n" +
        "    ON\n" +
        "    B.ID = BSI.BOM_ID\n" +
        "  WHERE\n" +
        "    B.ID = :bomId\n" +
        "\n" +
        "  UNION ALL\n" +
        "\n" +
        "  SELECT\n" +
        "    BSI.SUB_PRODUCT_ID\n" +
        "  FROM\n" +
        "    PRODUCTS P\n" +
        "    JOIN\n" +
        "    BOM B\n" +
        "    ON\n" +
        "    P.ID = B.PRODUCT_ID\n" +
        "\n" +
        "    JOIN\n" +
        "    BOM_SPEC_ITEM BSI\n" +
        "    ON\n" +
        "    B.ID = BSI.BOM_ID\n" +
        ")\n" +
        "SELECT DISTINCT\n" +
        "  P.*\n" +
        "FROM\n" +
        "  PRODUCT P\n" +
        "  JOIN\n" +
        "  PRODUCTS RP\n" +
        "  ON\n" +
        "  RP.ID = P.ID"
    )
    List<EcoProduct> findHierarchyProductListByBomId(@Param("bomId") Long bomId);
}