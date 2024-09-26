package eco.repository;

import eco.entity.EcoBom;
import eco.entity.EcoBomAttribute;
import eco.entity.EcoLaunch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoBomAttributeRepository extends JpaRepository<EcoBomAttribute, Long> {

    @Query("select " +
            "   case when ba.acceptDate is null then false else true end as accepted " +
            "from EcoBomAttribute ba " +
            "   where ba.bom = ?1 AND ba.launch = ?2"
    )
    Boolean isAccepted(EcoBom bom, EcoLaunch launch);

    EcoBomAttribute findFirstByBomOrderByLaunch_YearDescLaunch_NumberInYearDesc(EcoBom bom);
    @Query(nativeQuery = true, value = "SELECT\n" +
        "  ba.*\n" +
        "FROM \n" +
        "  (\n" +
        "    SELECT\n" +
        "      ba.id,\n" +
        "      ROW_NUMBER() OVER(PARTITION BY ba.launch_id, b.product_id ORDER BY ba.approve_date DESC, b.major DESC, b.minor DESC, b.modification DESC) rn\n" +
        "    FROM\n" +
        "      bom_attribute ba\n" +
        "      JOIN\n" +
        "      bom b ON ba.bom_id = b.id\n" +
        "    WHERE\n" +
        "      ba.approve_date IS NOT NULL\n" +
        "  ) res\n" +
        "  JOIN\n" +
        "  bom_attribute ba\n" +
        "  ON ba.id = res.id\n" +
        "  AND res.rn = 1")
    List<EcoBomAttribute> findAllByApproveDateIsNotNull();
}